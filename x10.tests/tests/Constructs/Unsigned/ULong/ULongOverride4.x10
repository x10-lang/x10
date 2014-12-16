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
 * Test overriding of methods with ULong parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongOverride4 extends x10Test {
    static interface I[T] {
	def f(x:T) : String;
    }

    static class J[T] implements I[T] {
	public def f(x:T) { return "J:" + x.typeName(); }
    }

    static class K extends J[Long] {}
    static class L extends K {
	public def f(x:Long) { return "L:Long"; }
	public def f(x:ULong) { return "L:ULong"; }
    }

    static class M extends J[ULong] {}
    static class N extends M {
	public def f(x:Long) { return "N:Long"; }
	public def f(x:ULong) { return "N:ULong"; }
    }

    public def run(): boolean = {
	val ji = new J[Long]();
	val ju = new J[ULong]();
	val k = new K();
	val l = new L();
	val m = new M();
	val n = new N();
	assert ji.f(1l).equals("J:x10.lang.Long");
	assert ju.f(1ul).equals("J:x10.lang.ULong");
	assert k.f(1l).equals("J:x10.lang.Long");
	assert l.f(1l).equals("L:Long");
	assert l.f(1ul).equals("L:ULong");
	assert m.f(1ul).equals("J:x10.lang.ULong");
	assert n.f(1l).equals("N:Long");
	assert n.f(1ul).equals("N:ULong");
	return true;
    }

    public static def main(Rail[String]) {
        new ULongOverride4().execute();
    }
}
