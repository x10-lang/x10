/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;

// NUM_PLACES: 4 

/**
 * Test that exceptions during deserialization do not hang X10
 * Pattern: nested loop of at (p) async at (q)
 */
public class DeserializationFailure4 extends x10Test {

    static class BoomBoom extends Exception {}

    static class TimeBomb implements CustomSerialization {
        val target:Place;

        def this(t:Place) { target = t; }

        public def this(d:Deserializer) {
            target = d.readAny() as Place;
            if (target == here) {
               Console.OUT.println("BOOM! from "+here);
               throw new BoomBoom();
            }
        }

        public def serialize(s:Serializer) {
            s.writeAny(target);
        }
    }

    public def run():boolean {
       var passed:boolean = true;

       for (victim in Place.places()) {
           val tb = new TimeBomb(victim);
           try {
               finish for (p in Place.places()) {
                   at (p) async {
                       for (q in Place.places()) {
                           at (q) async { 
                               Console.OUT.println(here+" received timebomb with target "+tb.target);
                           }
                       }
                   }
               }
               Console.OUT.println("Sub-test fail: exception was not raised with victim "+victim);
               passed = false;
            } catch (e:MultipleExceptions) {
                val expected = Place.numPlaces();
                if (e.exceptions().size == expected) {
                    Console.OUT.println("Sub-test pass: got exactly "+expected+" exceptions");
                } else {
                    Console.OUT.println("Sub-test fail: got "+e.exceptions().size+" exceptions; not "+expected);
                    passed = false;
                }
            }
       }                          
       return passed;
    }

    public static def main(Rail[String]) {
        new DeserializationFailure4().execute();
    }
}
