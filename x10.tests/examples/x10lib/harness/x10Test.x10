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

import x10.compiler.WS;
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
    @WS abstract public def run(): boolean;

    public def executeAsync() {
        val b = new Cell[Boolean](false);  
        try {
            finish async b(this.run());
        } catch (e: Throwable) {
            e.printStackTrace();
        }
        reportResult(b());
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

    // Convert a partial path and a file name into
    // an absolute path to the file.
    // If being run under the nightly test harness, then
    // X10_HOME will be set in the environment and will be used
    // to build up a path.  If it is not set, the simple path ./fileName
    // will be returned instead.
    public static def pathCombine(prefix:String, file:String):String {
        val env = System.getenv();
        val home = env.getOrElse("X10_HOME", null);
        if (home == null) {
            return "./"+file;
        } else {
            return home+"/x10.tests/examples/"+prefix+"/"+file;
        }
    }

    public static PREFIX: String = "++++++ ";

    public static def success(): void = {
        println(PREFIX+"Test succeeded.");
	   at (Place.FIRST_PLACE) 
	     System.setExitCode(0);
    }

    public static def failure(): void = {
        println(PREFIX+"Test failed.");
        at (Place.FIRST_PLACE)
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

    private var myRand:Random = new Random(1L);

    /**
     * Return a random integer between lb and ub (inclusive)
     */
    protected def ranInt(lb: int, ub: int): int = {
        return lb + myRand.nextInt(ub-lb+1);
    }

    protected var result: boolean;
    protected final def check[T](test:String, actual:T, expected:T) = {
	result = actual == expected;
	println(test + (result ? " succeeds: got "
			: " fails: exepected " + expected + ", got " )
		+ actual);
    }

    protected static def println(s:String) { x10.io.Console.OUT.println(s); }

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

        public abstract def test() : void;

        public val errors : List[String] = new ArrayList[String]();

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
          if ((a as Any) == null || (b as Any) == null) return false;
          return a.equals(b);
        }

        public final def eqeq[T](a:T, b:T, loc: String) {
          yes(a==b, loc + " for (" + a + ").==((" + b + "))");
        }

        public final def eq[T](a:T, b:T, loc: String) {
          yes(equals[T](a,b), loc + " for (" + a + ").equals((" + b + "))");
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
