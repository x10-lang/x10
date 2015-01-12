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


/**
 * Check lazy, per-field, per-place initialization semantics of static fields.
 * Valid for X10 2.2.3 and later.
 * 
 * @author mtake 7/2012
 */
public class InitStaticField3b extends x10Test {

	static val a = null;
	static val b = 1n;
	static val c = "abc";
        
    static val d = null as Any;

    static val f = 1n as Any;
    static val g = 1n as Int;
    static val h = 1n as Comparable[Int];
    static val i = 1n as Arithmetic[Int];
    
    static val j = "abc" as Any;
    static val l = "abc" as String;
    static val n = "abc" as Comparable[String];
    

    public def run():Boolean {
        chk(a == null);
        chk(b == 1n);
        chk(c.equals("abc"));
        chk(d == null);
        chk(f == 1n);
        chk(g == 1n);
        chk(h == 1n);
        chk(i == 1n);
        chk(j.equals("abc"));
        chk(l.equals("abc"));
        chk(n.equals("abc"));

        return true;
    }

    public static def main(Rail[String]) {
        new InitStaticField3b().execute();
    }

}
