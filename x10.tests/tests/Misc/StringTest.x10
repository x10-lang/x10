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
 * Testing quotes in strings.
 */
public class StringTest extends x10Test {

    public var v: int;
    public def this(): StringTest = {
        v = 10n;
    }

    public def run(): boolean = {
        var foo: String = "the number is "+v;
        if (!(v == 10n && foo.equals("the number is "+"10"))) return false;
        if (foo.charAt(2n) != 'e') return false;

        val start = "Start Twelve  ";
        chk(start.trim().equals("Start Twelve"));
        val end = " Total Ten.";
        chk(end.trim().equals("Total Ten."));
        val bothEnds = "  Four  ";
        chk(bothEnds.trim().equals("Four"));

        val s1 = "abc";
        chk(s1 instanceof String);
        chk((s1 as Any) instanceof String);
        val a1 = s1 as Any;
        chk(a1 instanceof String);
        chk((a1 as String) instanceof String);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new StringTest().execute();
    }
}
