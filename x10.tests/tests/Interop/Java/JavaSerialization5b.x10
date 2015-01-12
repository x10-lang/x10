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

// MANAGED_X10_ONLY

public class JavaSerialization5b extends x10Test {

    static class C1 {
        val v = 1n;
    }

    static class C2 {
        val v:Any = 1n;
    }

    static class C3 {
        val v = 1un;
    }

    static class C4 {
        val v:Any = 1un;
    }

    static class C5 {
        val v = new java.lang.Integer(1n);
    }

    static class C6 {
        val v:Any = new java.lang.Integer(1n);
    }

    static def test1():void {
        val c = new C1();
        at (Place.places().next(here)) {
            val v = c.v;
            chk("x10.lang.Int".equals(v.typeName()));
            chk(v == 1n);
        }
    }

    static def test2():void {
        val c = new C2();
        at (Place.places().next(here)) {
            val v = c.v;
            chk("x10.lang.Int".equals(v.typeName()));
            chk(v == 1n);
        }
    }

    static def test3():void {
        val c = new C3();
        at (Place.places().next(here)) {
            val v = c.v;
            chk("x10.lang.UInt".equals(v.typeName()));
            chk(v == 1un);
        }
    }

    static def test4():void {
        val c = new C4();
        at (Place.places().next(here)) {
            val v = c.v;
            chk("x10.lang.UInt".equals(v.typeName()));
            chk(v == 1un);
        }
    }

    static def test5():void {
        val c = new C5();
        at (Place.places().next(here)) {
            val v = c.v;
            chk("java.lang.Integer".equals(v.typeName()));
            chk(v.equals(java.lang.Integer.valueOf(1n)));
        }
    }

    static def test6():void {
        val c = new C6();
        at (Place.places().next(here)) {
            val v = c.v;
            chk("java.lang.Integer".equals(v.typeName()));
            chk(v.equals(java.lang.Integer.valueOf(1n)));
        }
    }

    public def run(): Boolean = {
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaSerialization5b().execute();
    }

}
