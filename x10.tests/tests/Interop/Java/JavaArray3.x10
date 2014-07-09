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
import x10.interop.Java;

// MANAGED_X10_ONLY

public class JavaArray3 extends x10Test {

    static def test1():void {
        val o = Java.newArray[String](1n);
        o(0n) = "abc";
        at (Place.places().next(here)) {
            o.toString();
            val s = o(0n);
            chk("abc".equals(s));
        }               
    }
        
    static def test2():void {
        val o = Java.newArray[String](1n);
        o(0n) = "abc";
        val a:Any = o;
        at (Place.places().next(here)) {
            a.toString();
            val s = (a as Java.array[String])(0n);
            chk("abc".equals(s));
        }
    }
        
    public def run(): Boolean {
        test1();
        test2();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaArray3().execute();
    }

}
