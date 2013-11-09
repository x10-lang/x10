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
 * Test overriding of methods with UShort parameters.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortOverride4 extends x10Test {
    static interface I[T] {
	def f(x:T) : String;
    }

    static class J[T] implements I[T] {
	public def f(x:T) { return "J:" + x.typeName(); }
    }

    static class K extends J[Short] {}
    static class L extends K {
	public def f(x:Short) { return "L:Short"; }
	public def f(x:UShort) { return "L:UShort"; }
    }

    static class M extends J[UShort] {}
    static class N extends M {
	public def f(x:Short) { return "N:Short"; }
	public def f(x:UShort) { return "N:UShort"; }
    }

    public def run(): boolean = {
	val ji = new J[Short]();
	val ju = new J[UShort]();
	val k = new K();
	val l = new L();
	val m = new M();
	val n = new N();
	assert ji.f(1s).equals("J:x10.lang.Short");
	assert ju.f(1us).equals("J:x10.lang.UShort");
	assert k.f(1s).equals("J:x10.lang.Short");
	assert l.f(1s).equals("L:Short");
	assert l.f(1us).equals("L:UShort");
	assert m.f(1us).equals("J:x10.lang.UShort");
	assert n.f(1s).equals("N:Short");
	assert n.f(1us).equals("N:UShort");
	return true;
    }

    public static def main(Rail[String]) {
        new UShortOverride4().execute();
    }
}
