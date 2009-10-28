/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Struct test:
 *
 * == for structs is structural equality
 *
 * @author kemal, 12/2004
 */
public class StructTest2 extends x10Test {

    public def run(): boolean = {
        var r: Region = [0..9];
        var d: Dist = r->here;
        val f: foo = new foo();
        var x: myval = myval(1, Complex(2,3), f);
        var y: myval = myval(1, Complex(2,3), f);
        // even if x and y are different objects
        // their fields are equal, and thus they are ==
        x10.io.Console.OUT.println("1");
        if (x != y) return false;
        val one = Complex(1,1);
        val minusone = Complex(-1,-1);
        val t = x.cval+one+minusone;
        y = myval(x.intval, t, x.refval);
        // x and y are still equal
        x10.io.Console.OUT.println("2");
        if (x != y) return false;
        // objects with different values are not equal
        y = myval(2, Complex(6,3), f);
        x10.io.Console.OUT.println("3");
        if (x == y) return false;
        y = x;
        x.refval.w++;
        // ensure foo is treated as a reference object
        // so both x and y see the update
        x10.io.Console.OUT.println("4");
        if (y.refval.w != x.refval.w) return false;
        x10.io.Console.OUT.println("5");
        if (y.refval.w != 20) return false;
        val P0: Place = here;
        // the "place" of a value class instance is here
        var n: int;
        { val y0: myval = y;
            n = y0.intval;
            
        }
        x10.io.Console.OUT.println("6");
        return n != -1;
    }

    public static def main(var args: Rail[String]): void = {
        new StructTest2().execute();
    }

    static final struct myval {
      val intval: int;
      val cval: Complex;
      val refval: foo;

      def this(intval: int, cval: Complex, refval:foo) = {
        this.intval = intval;
        this.cval = cval;
        this.refval = refval;
      }
      public def typeName() = "myval";
   }

   static class foo {
     var w: int = 19;
   }
}
