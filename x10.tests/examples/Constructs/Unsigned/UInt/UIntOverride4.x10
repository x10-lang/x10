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
 * Test overriding of methods with UInt parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntOverride4 extends x10Test {
    static interface I[T] {
	def f(x:T) : String;
    }

    static class J[T] implements I[T] {
	public def f(x:T) { return "J:" + x.typeName(); }
    }

    static class K extends J[Int] {}
    static class L extends K {
	public def f(x:Int) { return "L:Int"; }
	public def f(x:UInt) { return "L:UInt"; }
    }

    static class M extends J[UInt] {}
    static class N extends M {
	public def f(x:Int) { return "N:Int"; }
	public def f(x:UInt) { return "N:UInt"; }
    }

    public def run(): boolean = {
	val ji = new J[Int]();
	val ju = new J[UInt]();
	val k = new K();
	val l = new L();
	val m = new M();
	val n = new N();
	assert ji.f(1n).equals("J:x10.lang.Int");
	assert ju.f(1un).equals("J:x10.lang.UInt");
	assert k.f(1n).equals("J:x10.lang.Int");
	assert l.f(1n).equals("L:Int");
	assert l.f(1un).equals("L:UInt");
	assert m.f(1un).equals("J:x10.lang.UInt");
	assert n.f(1n).equals("N:Int");
	assert n.f(1un).equals("N:UInt");
	return true;
    }

    public static def main(Rail[String]) {
        new UIntOverride4().execute();
    }
}
