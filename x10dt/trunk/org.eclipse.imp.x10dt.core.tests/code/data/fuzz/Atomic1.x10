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

/**
 * Some updates of cnt_broken may be lost,
 * since the read and write are not
 * inside the same atomic section.
 */
public class Atomic1 extends x10Test {

	var cnt: int = 0;
	var cnt_broken: int = 0;
	public const N: int = 100;
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
		val a = future(this) threadRun();
		val b = future(this) threadRun();
		val c = future(this) threadRun();
		val d = future(this) threadRun();
		val e = future(this) threadRun();
		val f = future(this) threadRun();
		val g = future(this) threadRun();
		val h = future(this) threadRun();
		val i = a.force();
		val j = b.force();
		val k = c.force();
		val l = d.force();
		val m = e.force();
		val n = f.force();
		val o = g.force();
		val p = h.force();
		var t1: int;
		var t2: int;
		atomic t1 = cnt;
		atomic t2 = cnt_broken;
		x10.io.Console.OUT.println("Atomic1: " + t1 + " =?= " + t2);
		return t1 == 8*N && t1 >= t2;
	}

	public static def main(var args: Rail[String]): void = {
		new Atomic1().execute();
	}
}
