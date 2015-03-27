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
 * Test calling with UShort parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortCall extends x10Test {

    static interface I {
	def f(a:UShort) : String;
	def f(a:Short) : String;
    }

    static class C implements I {
	public def f(a:UShort) = "ushort";
	public def f(a:Short) = "short";
    }

    public def f(a:UShort) {
	 return "ushort = " + a;
    }

    public def f(a:Short) {
	return "short = " + a;
    }

    public static def fs(a:UShort) {
	return "ushort" + a;
    }

    public static def fs(a:Short) {
	return "short" + a;
    }

    public def run(): boolean {
	var r : Boolean = true;
	val i0 = 0s;
	val u1 = 1us;
	if (!(f(i0).equals("short = 0"))) 	 	{ p(" !(f(i0).equals('short = 0')) "); r = false; }
	if (!(f(u1).equals("ushort = 1"))) 		{ p(" !(f(u1).equals('ushort = 1') "); r = false; }
	if (!(fs(i0).equals("short0")))			{ p(" !(fs(i0).equals('short0')))  "); r = false; }
	if (!(fs(u1).equals("ushort1")))			{ p(" !(fs(u1).equals('ushort1'))) "); r = false; }
	val c = new C();
	if (!(c.f(i0).equals("short")))			{ p(" !(c.f(i0).equals('short'))) "); r = false; }
	if (!(c.f(u1).equals("ushort")))			{ p(" !(c.f(u1).equals('ushort')) "); r = false; }
	val i : I = c;
	if (!(i.f(i0).equals("short")))			{ p(" !(i.f(i0).equals('short')) "); r = false; }
	if (!(i.f(u1).equals("ushort")))			{ p(" !(i.f(u1).equals('ushort') "); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new UShortCall().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
