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

import x10.io.Console;
import harness.x10Test;
import x10.regionarray.*;

// MANAGED_X10_ONLY

/*
 * Narrowed-down version from RootException11.x10.
 * Run with X10_NPLACES=2 to reproduce the problem. 
 */
class RootException11b extends x10Test {
        
    static class MyException1 extends Exception {
        val exceptions = new Array[Exception](0);
    }

    static class MyException2 extends Exception {
        val exceptions = new Array[Exception](1);
    }

    static class MyException3 extends Exception {
        val exceptions = new Array[Exception](1, new Exception());
    }

    static class MyException9 extends Exception {
        val exceptions = new Array[Exception](Region.make(1..2, 3..4, 5..6), new Exception());
    }

    public def test0() {
        var ok:Boolean = false;
        try {
            finish
                at (Place.places().next(here))
                    async
                        throw new Exception();
        } catch (ex: Exception) {
            ok = true;
            Console.OUT.println(ex);
        }
        return ok;
    }
    
    public def test1() {
        var ok:Boolean = false;
        try {
            finish
                at (Place.places().next(here))
                    async
                        throw new MyException1();
        } catch (ex: Exception) {
            ok = true;
            Console.OUT.println(ex);
        }
        return ok;
    }
        
    public def test2() {
        var ok:Boolean = false;
        try {
            finish
                at (Place.places().next(here))
                    async
                        throw new MyException2();
        } catch (ex: Exception) {
            ok = true;
            Console.OUT.println(ex);
        }
        return ok;
    }
        
    public def test3() {
        var ok:Boolean = false;
        try {
            finish
                at (Place.places().next(here))
                    async
                        throw new MyException3();
        } catch (ex: Exception) {
            ok = true;
            Console.OUT.println(ex);
        }
        return ok;
    }
        
    public def test9() {
        var ok:Boolean = false;
        try {
            finish
                at (Place.places().next(here))
                    async
                        throw new MyException9();
        } catch (ex: Exception) {
            ok = true;
            Console.OUT.println(ex);
        }
        return ok;
    }
    
    public def run() {
        val oks = new x10.util.ArrayList[Boolean]();
        var ok:Boolean;
        
        ok = test0();
        if (!ok) Console.OUT.println("Error in test0");
        oks.add(ok);

        ok = test1();
        if (!ok) Console.OUT.println("Error in test1");
        oks.add(ok);

        ok = test2();
        if (!ok) Console.OUT.println("Error in test2");
        oks.add(ok);

        ok = test3();
        if (!ok) Console.OUT.println("Error in test3");
        oks.add(ok);

        ok = test9();
        if (!ok) Console.OUT.println("Error in test9");
        oks.add(ok);

        for (thisOk in oks) {
            if (!thisOk) return false;
        }
        return true;
    }

    public static def main(args: Rail[String]):void {
        new RootException11b().execute();
    }

}
