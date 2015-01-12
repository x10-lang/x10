/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.io.CustomSerialization;
import x10.io.Deserializer;
import x10.io.Serializer;

// NUM_PLACES: 4 

/**
 * Test that exceptions during serialization do not hang X10
 * Pattern: PlaceGroup.broadcastFlat
 */
public class SerializationFailure5 extends x10Test {

    static class BoomBoom extends Exception {}

    static class TimeBomb implements CustomSerialization {
        val target:Place;

        def this(t:Place) { target = t; }

        public def this(d:Deserializer) {
            target = d.readAny() as Place;
        }

        public def serialize(s:Serializer) {
            s.writeAny(target);
            if (target == here) {
               Console.OUT.println("BOOM! from "+here);
               throw new BoomBoom();
            }
        }
    }

    public def run():boolean {
       var passed:boolean = true;

       for (victim in Place.places()) {
           val tb = new TimeBomb(victim);
           try {
               Place.places().broadcastFlat(()=> {
                   Console.OUT.println(here+" received timebomb with target "+tb.target);
               });
               if (victim == here) {
                   Console.OUT.println("Sub-test fail: exception was not raised with victim "+victim);
                   passed = false;
               }
            } catch (e:BoomBoom) {
                if (victim != here) {
                    Console.OUT.println("Sub-test fail: got supurious exception ");
                    passed = false;
                }
            }
       }                      
       return passed;
    }

    public static def main(Rail[String]) {
        new SerializationFailure5().execute();
    }
}
