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

import harness.x10Test;
import x10.interop.Java;

// MANAGED_X10_ONLY

public class JavaArray3 extends x10Test {

    static def test1():void {
        val o = Java.newArray[String](1);
        o(0) = "abc";
        at (here.next()) {
            o.toString();
            val s = o(0);
            chk("abc".equals(s));
        }               
    }
        
    static def test2():void {
        val o = Java.newArray[String](1);
        o(0) = "abc";
        val a:Any = o;
        at (here.next()) {
            a.toString();
            val s = (a as Java.array[String])(0);
            chk("abc".equals(s));
        }
    }
        
    public def run(): Boolean {
        test1();
        test2();
        return true;
    }

    public static def main(args: Array[String](1)) {
        new JavaArray3().execute();
    }

}
