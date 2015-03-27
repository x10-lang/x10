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
import x10.regionarray.*;

// vj Nov 9, 2009. This will throw a compiler error until we fix
// the XTerm translator so it can accept d(p) as a term, where d is
// a distribution and p a place.

public class PlaceCast2 extends x10Test {
    var nplaces: long = 0L;
    private val root = GlobalRef[PlaceCast2](this);
    public def run(): boolean {
            val d = Dist.makeUnique();
            x10.io.Console.OUT.println("num places = " + Place.numPlaces());
            val root = this.root;
            val disagree = DistArray.make[BoxedBoolean](d, ([p]: Point): BoxedBoolean => {
                x10.io.Console.OUT.println("The currentplace is:" + here);
                return new BoxedBoolean();
            });
            finish ateach (p  in d) {
               // remember if here and d[p] disagree
               // at any activity at any place
               try {
                val dp = d(p);
                val x  = disagree(p).home==here; 
                at (root) atomic  root().nplaces++;  
               } catch (x: BadPlaceException)  {
                  x10.io.Console.OUT.println("Caught bad place exception for " + p);
               } catch (x: ClassCastException) {
                  Console.OUT.println("Caught class cast exception for " + p);
              }
        }
        x10.io.Console.OUT.println("nplaces == " + nplaces);
        return nplaces == Place.numPlaces();
    }

    public static def main(Rail[String]) {
        new PlaceCast2().execute();
    }

    static class BoxedBoolean {
        var v: boolean = false;
        val home = here;
    }
}
