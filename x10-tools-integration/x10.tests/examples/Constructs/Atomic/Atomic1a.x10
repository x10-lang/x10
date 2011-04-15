/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

import x10.util.concurrent.Future;
/**
 * Some updates of cnt_broken may be lost,
 * since the read and write are not
 * inside the same atomic section.
 */
public class Atomic1a extends x10Test {
    private val root = GlobalRef[Atomic1a](this);
	transient var cnt: int = 0;
	transient var cnt_broken: int = 0;
	public static N: int = 100;
	def threadRun(): int = {
		for (var i: int = 0; i < N; ++i) {
			var t: int;
			atomic t = cnt_broken;
			atomic ++cnt;
			atomic cnt_broken = t + 1;
		}
		return 0;
	}

	public def run(): boolean = {
		val root = this.root;
		val a = ()=> root().threadRun();
		val b = ()=>  root().threadRun();
		val c = ()=> root().threadRun();
		val d =  ()=> root().threadRun();
		val e =  ()=> root().threadRun();
		val f =  ()=> root().threadRun();
		val g =  ()=>  root().threadRun();
		val h =  ()=>  root().threadRun();
		val i = a();
		val j = b();
		val k = c();
		val l = d();
		val m = e();
		val n = f();
		val o = g();
		val p = h();
		var t1: int;
		var t2: int;
		atomic t1 = cnt;
		atomic t2 = cnt_broken;
		// x10.io.Console.OUT.println("Atomic1: " + t1 + " =?= " + t2);
		return t1 == 8*N && t1 >= t2;
	}

	public static def main(Array[String](1)) {
		new Atomic1a().execute();
	}
}
