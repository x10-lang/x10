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
 * Test calling with UInt parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntCall extends x10Test {

    static interface I {
	def f(a:UInt) : String;
	def f(a:Int) : String;
    }

    static class C implements I {
	public def f(a:UInt) = "uint";
	public def f(a:Int) = "int";
    }

    public def f(a:UInt) {
	 return "uint = " + a;
    }

    public def f(a:Int) {
	return "int = " + a;
    }

    public static def fs(a:UInt) {
	return "uint" + a;
    }

    public static def fs(a:Int) {
	return "int" + a;
    }

    public def run(): boolean = {
	var r : Boolean = true;
	val i0 = 0n;
	val u1 = 1un;
	if (!(f(i0).equals("int = 0"))) 	 	{ p(" !(f(i0).equals('int = 0')) "); r = false; }
	if (!(f(u1).equals("uint = 1"))) 		{ p(" !(f(u1).equals('uint = 1') "); r = false; }
	if (!(fs(i0).equals("int0")))			{ p(" !(fs(i0).equals('int0')))  "); r = false; }
	if (!(fs(u1).equals("uint1")))			{ p(" !(fs(u1).equals('uint1'))) "); r = false; }
	val c = new C();
	if (!(c.f(i0).equals("int")))			{ p(" !(c.f(i0).equals('int'))) "); r = false; }
	if (!(c.f(u1).equals("uint")))			{ p(" !(c.f(u1).equals('uint')) "); r = false; }
	val i : I = c;
	if (!(i.f(i0).equals("int")))			{ p(" !(i.f(i0).equals('int')) "); r = false; }
	if (!(i.f(u1).equals("uint")))			{ p(" !(i.f(u1).equals('uint') "); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new UIntCall().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
