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
 * @author mtake 7/2012
 */
public class Any1 extends x10Test {

    static class C {}
    static struct S {}

    public def run():Boolean {
        var ok: Boolean = true;

        val c = new C();
        val ct = c.typeName();
        val cs = c.toString();
        val ch = c.hashCode();
        Console.OUT.println(ct);
        Console.OUT.println(cs);
        Console.OUT.println(ch);
        chk(c.equals(c));

        val s = new S();
        val st = s.typeName();
        val ss = s.toString();
        val sh = s.hashCode();
        Console.OUT.println(st);
        Console.OUT.println(ss);
        Console.OUT.println(sh);
        chk(s.equals(s));

        return ok;
    }

    public static def main(Rail[String]) {
        new Any1().execute();
    }

}
