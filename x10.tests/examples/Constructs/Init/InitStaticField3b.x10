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


/**
 * Check lazy, per-field, per-place initialization semantics of static fields.
 * Valid for X10 2.2.3 and later.
 * 
 * @author mtake 7/2012
 */
public class InitStaticField3b extends x10Test {

	static val a = null;
	static val b = 1;
	static val c = "abc";
        
    static val d = null as Any;
    static val e = null as Object;

    static val f = 1 as Any;
    static val g = 1 as Int;
    static val h = 1 as Comparable[Int];
    static val i = 1 as Arithmetic[Int];
    
    static val j = "abc" as Any;
    static val k = "abc" as Object;
    static val l = "abc" as String;
    static val m = "abc" as (Int)=>Char;
    static val n = "abc" as Comparable[String];
    

    public def run():Boolean {
        chk(a == null);
        chk(b == 1);
        chk(c.equals("abc"));
        chk(d == null);
        chk(e == null);
        chk(f == 1);
        chk(g == 1);
        chk(h == 1);
        chk(i == 1);
        chk(j.equals("abc"));
        chk(k.equals("abc"));
        chk(l.equals("abc"));
        chk(m.equals("abc"));
        chk(n.equals("abc"));

        return true;
    }

    public static def main(Array[String](1)) {
        new InitStaticField3b().execute();
    }

}
