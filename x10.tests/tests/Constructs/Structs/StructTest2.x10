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
import x10.regionarray.Region;

/**
 * Struct test:
 *
 * == for structs is structural equality
 *
 * @author kemal, 12/2004
 */
public class StructTest2 extends x10Test {

    public def run(): boolean = {
        val r = Region.make(0, 9);
        val d = r->here;
        val f = new foo();
        var x:myval = myval(1, Complex(2,3), f);
        var y:myval = myval(1, Complex(2,3), f);
        // even if x and y are different objects
        // their fields are equal, and thus they are ==
        x10.io.Console.OUT.println("1");
        if (x != y) return false;
        val one = Complex(1,1);
        val minusone = Complex(-1,-1);
        val t = x.cval+one+minusone;
        y = myval(x.lval, t, x.refval);
        // x and y are still equal
        x10.io.Console.OUT.println("2");
        if (x != y) return false;
        // objects with different values are not equal
        y = myval(2, Complex(6,3), f);
        x10.io.Console.OUT.println("3");
        if (x == y) return false;
        y = x;
	val tmp = x.refval;
        tmp.w++;
        // ensure foo is treated as a reference object
        // so both x and y see the update
        x10.io.Console.OUT.println("4");
        if (y.refval.getW() != x.refval.getW()) return false;
        x10.io.Console.OUT.println("5");
        if (y.refval.getW() != 20) return false;
        val P0: Place = here;
        // the "place" of a value class instance is here
        var n: long;
        { val y0: myval = y;
            n = y0.lval;
            
        }
        x10.io.Console.OUT.println("6");
        return n != -1;
    }

    public static def main(var args: Rail[String]): void = {
        new StructTest2().execute();
    }

    static final struct myval {
      val lval: long;
      val cval: Complex;
      val refval: foo;

      def this(lval: long, cval: Complex, refval:foo) = {
        this.lval = lval;
        this.cval = cval;
        this.refval = refval;
      }
   }

   static class foo {
     var w: long = 19;

     def getW() {
	return w;
     }

   }
}
