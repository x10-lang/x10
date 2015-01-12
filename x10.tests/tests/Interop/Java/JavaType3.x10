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
import x10.interop.Java;

// MANAGED_X10_ONLY

public class JavaType3 extends x10Test {

    def test1() {
        val a:Any = Java.newArray[Int](1n);
        val s = a.typeName();
        Console.OUT.println(s);
        chk("x10.interop.Java.array[x10.lang.Int]".equals(s));
        chk(a instanceof Java.array[Int]);
    }

    def test2() {
    	val a:Any = Java.newArray[Any](1n);
    	val s = a.typeName();
    	Console.OUT.println(s);
    	chk("x10.interop.Java.array[x10.lang.Any]".equals(s));
    	chk(a instanceof Java.array[Any]);
    }

    def test3() {
    	val a:Any = Java.newArray[String](1n);
    	val s = a.typeName();
    	Console.OUT.println(s);
    	chk("x10.interop.Java.array[x10.lang.String]".equals(s));
    	chk(a instanceof Java.array[String]);
    }

    public def run(): Boolean = {
        test1();
        test2();
        test3();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaType3().execute();
    }
}
