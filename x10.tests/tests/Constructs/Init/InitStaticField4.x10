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


/**
 * Check lazy, per-field, per-place initialization semantics of static fields.
 * Valid for X10 2.2.3 and later.
 * 
 * @author mtake 7/2012
 */
public class InitStaticField4 extends x10Test {

    static class C {
        def this() {}
    }

    static class D {
        def this() {
            count()++;
            throw new Exception();
        }
    }

    static val count = new Cell[Int](0n);
    static val c = new C();
    static val d = new D();

    public def run():Boolean {
        var ok:Boolean = true;

        try {
            ok = false;
            val c = InitStaticField4.c;
            ok = true;
        } catch (e:ExceptionInInitializer) {
            ok = false;
            Console.OUT.println("BUG: ExceptionInInitializer was thrown for c!");
        } catch (e:Exception) {
            ok = false;
            Console.OUT.println("BUG: something other than ExceptionInInitializer was thrown for c!");
        }

        try {
            val d = InitStaticField4.d;
            ok = false;
            Console.OUT.println("BUG: no exception was thrown for d!");
        } catch (e:ExceptionInInitializer) {
            // check ExceptionInInitializer is thrown
        } catch (e:Exception) {
            ok = false;
            Console.OUT.println("BUG: something other than ExceptionInInitializer was thrown for d!");
        }

        try {
            val d = InitStaticField4.d;
            ok = false;
            Console.OUT.println("BUG: no exception was thrown for d!");
        } catch (e:ExceptionInInitializer) {
            // check ExceptionInInitializer is thrown
        } catch (e:Exception) {
            ok = false;
            Console.OUT.println("BUG: something other than ExceptionInInitializer was thrown for d!");
        }

        // check atmost once semantics
        if (count() != 1n) {
            Console.OUT.println("BUG: initializer expression was evaluated multiple times in a place for d!");
        }
        
        return ok && count() == 1n;
    }

    public static def main(Rail[String]) {
        new InitStaticField4().execute();
    }

}
