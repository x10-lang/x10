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
 * Test calling with UByte parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteCall extends x10Test {

    static interface I {
	def f(a:UByte) : String;
	def f(a:Byte) : String;
    }

    static class C implements I {
	public def f(a:UByte) = "ubyte";
	public def f(a:Byte) = "byte";
    }

    public def f(a:UByte) {
	 return "ubyte = " + a;
    }

    public def f(a:Byte) {
	return "byte = " + a;
    }

    public static def fs(a:UByte) {
	return "ubyte" + a;
    }

    public static def fs(a:Byte) {
	return "byte" + a;
    }

    public def run(): boolean = {
	var r : Boolean = true;
	val i0 = 0y;
	val u1 = 1uy;
	if (!(f(i0).equals("byte = 0"))) 	 	{ p(" !(f(i0).equals('byte = 0')) "); r = false; }
	if (!(f(u1).equals("ubyte = 1"))) 		{ p(" !(f(u1).equals('ubyte = 1') "); r = false; }
	if (!(fs(i0).equals("byte0")))			{ p(" !(fs(i0).equals('byte0')))  "); r = false; }
	if (!(fs(u1).equals("ubyte1")))			{ p(" !(fs(u1).equals('ubyte1'))) "); r = false; }
	val c = new C();
	if (!(c.f(i0).equals("byte")))			{ p(" !(c.f(i0).equals('byte'))) "); r = false; }
	if (!(c.f(u1).equals("ubyte")))			{ p(" !(c.f(u1).equals('ubyte')) "); r = false; }
	val i : I = c;
	if (!(i.f(i0).equals("byte")))			{ p(" !(i.f(i0).equals('byte')) "); r = false; }
	if (!(i.f(u1).equals("ubyte")))			{ p(" !(i.f(u1).equals('ubyte') "); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new UByteCall().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
