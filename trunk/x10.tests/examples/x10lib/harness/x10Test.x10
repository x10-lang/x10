/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package harness;

import x10.util.*;
import x10.io.Console;


/**
 * Test harness abstract class.
 */

abstract public class x10Test {

    /**
     * The body of the test.
     * @return true on success, false on failure
     */
    abstract public def run(): boolean;

    public def executeAsync() {
        val b: Rail[boolean]! = [ false ]; // use a rail until we have shared locals working
        try {
            finish async b(0) = this.run();
        } catch (e: Throwable) {
            e.printStackTrace();
        }
        reportResult(b(0));
    }

    public def execute(): void = {
        var b: boolean = false;
        try {
            finish b = this.run();
        } catch (e: Throwable) {
            e.printStackTrace();
        }
        reportResult(b);
    }

    public const PREFIX: String = "++++++ ";

    public static def success(): void = {
        println(PREFIX+"Test succeeded.");
	System.setExitCode(0);
    }

    public static def failure(): void = {
        println(PREFIX+"Test failed.");
        System.setExitCode(1);
    }

    protected static def reportResult(b: boolean): void = {
        if (b) success(); else failure();
    }

    /**
     * Check if a given condition is true, and throw an error if not.
     */
    public static def chk(b: boolean): void = {
        if (!b) throw new Error();
    }

    /**
     * Check if a given condition is true, and throw an error with a given
     * message if not.
     */
    public static def chk(b: boolean, s: String): void = {
        if (!b) throw new Error(s);
    }

    private var myRand:Random! = new Random(1L);

    /**
     * Return a random integer between lb and ub (inclusive)
     */

    protected def ranInt(lb: int, ub: int): int = {
        return lb + myRand.nextInt(ub-lb+1);
    }

    protected var result: boolean;
    protected def check[T](test:String, actual:T, expected:T) = {
	result = actual == expected;
	println(test + (result ? " succeeds: got "
			: " fails: exepected " + expected + ", got " )
		+ actual);
    }


    protected static def println(s:String) = x10.io.Console.OUT.println(s);

    public static abstract class BardTest extends x10Test {

        static val MAX_ERRORS_TO_PRINT = 10;

        public def run() : Boolean {
       x10.io.Console.OUT.println("(Bard)I am about to test a " + this.typeName());
           this.test();
           val noErr = errors.size() == 0;
           if (!noErr) {
              x10.io.Console.OUT.println("(Bard)FAIL - " + this.typeName() + "\n" + this.errorString());
           }
           else {
              x10.io.Console.OUT.println("(Bard) SUCCESS");
           }
           x10.io.Console.OUT.println("noErr = " + noErr);
           return noErr;
        }

        public abstract def test() : Void;

        public val errors : List[String]! = new ArrayList[String]();

        public def errorString() : String = {
          var s : String = "";
          var i : Int = 1;
          for (es in errors) {
            s += "  " + i + ". " + es + "\n";
            i ++;
            if (i > MAX_ERRORS_TO_PRINT) {
               s += "  (total of " + (errors.size()) + " failures)\n"; 
               break;
            }
          }
          return s;
        }

        public atomic final def err(loc:String) {
          errors.add(loc);
        }

        public final def no(b:Boolean, loc: String) {
          if(b) err(loc);
        }

        public final def yes(b: Boolean, loc: String) {
          no(!b, loc);
        }

        public final def equals[T](a:T, b:T):Boolean {
          if (a == b) return true;
          if (T <: Object) {
             val aa = a as Object;
             val bb = b as Object;
             if (aa == null || bb == null) return false;
          }
          //if (a == null || b == null) return false;
          return a.equals(b);
        }

        public final def eqeq[T](a:T, b:T, loc: String) {
          yes(a==b, loc + " for (" + a + ").==((" + b + "))");
        }

        public final def eq[T](a:T, b:T, loc: String) {
          yes(equals[T](a,b), loc + " for (" + a + ").equals((" + b + "))");
        }


        public final def eqValRails[T](a:ValRail[T], b:ValRail[T], loc: String) {
          for (var i : Int = 0; i < a.length; i++) {
            val ai:T = a(i);
            val bi:T = b(i);  
            yes(equals[T](ai,bi), loc + "(" + i + ")"+ " for (" + ai + ").equals((" + bi + "))");
          }
        }



        public final def neqeq[T](a:T, b:T, loc: String) {
          no(a==b, loc + " for !((" + a + ").equals((" + b + ")))");
        }
        public final def neq[T](a:T, b:T, loc: String) {
          no(equals[T](a,b), loc + " for !((" + a + ").equals((" + b + ")))");
        }

        public final def say(msg: String) { 
          Console.OUT.println("(" + this.typeName() + ") " + msg);
        }

        public static def str[T](r : ValRail[T], sep: String) : String {
           var s : String = "";
           for(var i : Int = 0; i < r.length; i++) {
              if (i > 0) s += sep;
              s += r(i);
           }
           return s;
        }

        public static def never():Boolean = false;
        public static def always():Boolean = true;

        public static def eq(a:Point, b:Point) {
          if (a.rank != b.rank) return false;
          for(var i : Int = 0; i < a.rank; i++) 
             if (a(i) != b(i)) return false;
          return true;
        }  

    }
    

}
