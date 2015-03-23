/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */


import harness.x10Test;
import x10.util.concurrent.Future;

/**
 * A test case that illustrates that deadlock is possible
 * with futures, by creating a circular wait-for dependency.
 *
 * A0 spawns future activities A1 and A2 and saves the "future"
 * handles from A1 and A2 in global future variables f1 and f2,
 * respectively.
 *
 * A1 sleeps a sufficient time to ensure A0 is done setting f2, then
 * reads global variable f2 and waits for the corresponding activity
 * (A2) to finish.
 *
 * A2 also sleeps a sufficient time to ensure A0 is done setting f1,
 * and then reads global variable f1 and waits for the corresponding
 * activity (A1) to finish.
 *
 * In the meantime A0 starts waiting for A1 to finish.
 *
 * Expected result: must deadlock.
 *
 * NB: This test case uses a sleep timer to
 * choreograph events on second granularily.
 *
 * @author kemal 4/2005
 */

public class FutureDeadlock_MustFailTimeout extends x10Test {
	var f1: Future[Int]= null;
	var f2: Future[Int] = null;

	def a1(): Int {
		System.sleep(5000); // to make deadlock occur deterministically
		var tmpf: Future[Int] = null;
		atomic tmpf = f2;
		x10.io.Console.OUT.println("Activity #1 about to force "+tmpf+" to wait for #2 to complete");
		return tmpf();
	}

	def a2(): int {
		System.sleep(5000); // to make deadlock occur deterministically
                var tmpf: Future[Int] = null;
		atomic tmpf = f1;
		x10.io.Console.OUT.println("Activity #2 about to force "+tmpf+" to wait for #1 to complete");
		return tmpf();
	}

	public def run(): boolean {
		val tmpf1  = Future.make[Int](() => a1());
		atomic f1 = tmpf1;
		val tmpf2 = Future.make[Int](() => a2());
		atomic f2 = tmpf2;
		x10.io.Console.OUT.println("Activity #0 spawned both activities #1 and #2, waiting for completion of #1");
		return tmpf1() == 42n;
	}

	public static def main(Rail[String]){
		new FutureDeadlock_MustFailTimeout().execute();
	}
}
