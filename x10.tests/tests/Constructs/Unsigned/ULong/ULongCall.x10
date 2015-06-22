/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import harness.x10Test;

/**
 * Test calling with ULong parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongCall extends x10Test {

    static interface I {
	def f(a:ULong) : String;
	def f(a:Long) : String;
    }

    static class C implements I {
	public def f(a:ULong) = "ulong";
	public def f(a:Long) = "long";
    }

    public def f(a:ULong) {
	 return "ulong = " + a;
    }

    public def f(a:Long) {
	return "long = " + a;
    }

    public static def fs(a:ULong) {
	return "ulong" + a;
    }

    public static def fs(a:Long) {
	return "long" + a;
    }

    public def run(): boolean {
	var r : Boolean = true;
	val i0 = 0l;
	val u1 = 1ul;
	if (!(f(i0).equals("long = 0"))) 	 	{ p(" !(f(i0).equals('long = 0')) "); r = false; }
	if (!(f(u1).equals("ulong = 1"))) 		{ p(" !(f(u1).equals('ulong = 1') "); r = false; }
	if (!(fs(i0).equals("long0")))			{ p(" !(fs(i0).equals('long0')))  "); r = false; }
	if (!(fs(u1).equals("ulong1")))			{ p(" !(fs(u1).equals('ulong1'))) "); r = false; }
	val c = new C();
	if (!(c.f(i0).equals("long")))			{ p(" !(c.f(i0).equals('long'))) "); r = false; }
	if (!(c.f(u1).equals("ulong")))			{ p(" !(c.f(u1).equals('ulong')) "); r = false; }
	val i : I = c;
	if (!(i.f(i0).equals("long")))			{ p(" !(i.f(i0).equals('long')) "); r = false; }
	if (!(i.f(u1).equals("ulong")))			{ p(" !(i.f(u1).equals('ulong') "); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new ULongCall().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
