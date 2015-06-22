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
 * Test overriding of methods with UByte parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteOverride4 extends x10Test {
    static interface I[T] {
	def f(x:T) : String;
    }

    static class J[T] implements I[T] {
	public def f(x:T) { return "J:" + x.typeName(); }
    }

    static class K extends J[Byte] {}
    static class L extends K {
	public def f(x:Byte) { return "L:Byte"; }
	public def f(x:UByte) { return "L:UByte"; }
    }

    static class M extends J[UByte] {}
    static class N extends M {
	public def f(x:Byte) { return "N:Byte"; }
	public def f(x:UByte) { return "N:UByte"; }
    }

    public def run(): boolean {
	val ji = new J[Byte]();
	val ju = new J[UByte]();
	val k = new K();
	val l = new L();
	val m = new M();
	val n = new N();
	assert ji.f(1y).equals("J:x10.lang.Byte");
	assert ju.f(1uy).equals("J:x10.lang.UByte");
	assert k.f(1y).equals("J:x10.lang.Byte");
	assert l.f(1y).equals("L:Byte");
	assert l.f(1uy).equals("L:UByte");
	assert m.f(1uy).equals("J:x10.lang.UByte");
	assert n.f(1y).equals("N:Byte");
	assert n.f(1uy).equals("N:UByte");
	return true;
    }

    public static def main(Rail[String]) {
        new UByteOverride4().execute();
    }
}
