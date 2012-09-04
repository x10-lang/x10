/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import x10.io.Console;
import harness.x10Test;

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

    public def test1() {
        var ok:Boolean = false;
        try {
            finish
                at (here.next())
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
                at (here.next())
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
                at (here.next())
                    async
                        throw new MyException3();
        } catch (ex: Exception) {
            ok = true;
            Console.OUT.println(ex);
        }
        return ok;
    }
        
    public def run(){
        val ok1 = test1();
        if (!ok1) Console.OUT.println("Error in test1");

        val ok2 = test2();
        if (!ok2) Console.OUT.println("Error in test2");

        val ok3 = test3();
        if (!ok3) Console.OUT.println("Error in test3");

        return ok1 && ok2 && ok3;
    }

    public static def main(args: Array[String](1)):void {
        new RootException11b().execute();
    }

}
