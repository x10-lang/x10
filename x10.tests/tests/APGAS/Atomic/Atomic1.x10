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

import x10.util.concurrent.Future;
/**
 * Some updates of cnt_broken may be lost,
 * since the read and write are not
 * inside the same atomic section.
 */
public class Atomic1 extends x10Test {
	transient var cnt: long = 0;
	transient var cnt_broken: long = 0;
	public static N: long = 100;
	def threadRun(): long = {
		for (i in 0..(N-1)) {
			var t: long;
			atomic t = cnt_broken;
			atomic ++cnt;
			atomic cnt_broken = t + 1;
		}
		return 0;
	}

	public def run(): boolean = {
		val a = Future.make[long](()=>threadRun());
		val b = Future.make[long](()=>threadRun());
		val c = Future.make[long](()=>threadRun());
		val d = Future.make[long](()=>threadRun());
		val e = Future.make[long](()=>threadRun());
		val f = Future.make[long](()=>threadRun());
		val g = Future.make[long](()=>threadRun());
		val h = Future.make[long](()=>threadRun());
		val i = a();
		val j = b();
		val k = c();
		val l = d();
		val m = e();
		val n = f();
		val o = g();
		val p = h();
		var t1: long;
		var t2: long;
		atomic t1 = cnt;
		atomic t2 = cnt_broken;
		// x10.io.Console.OUT.println("Atomic1: " + t1 + " =?= " + t2);
		return t1 == 8*N && t1 >= t2;
	}

	public static def main(Rail[String]) {
		new Atomic1().execute();
	}
}
