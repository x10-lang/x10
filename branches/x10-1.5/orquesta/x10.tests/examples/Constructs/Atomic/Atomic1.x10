/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

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
		var a: Future[int] = future(this) threadRun();
		var b: Future[int] = future(this) threadRun();
		var c: Future[int] = future(this) threadRun();
		var d: Future[int] = future(this) threadRun();
		var e: Future[int] = future(this) threadRun();
		var f: Future[int] = future(this) threadRun();
		var g: Future[int] = future(this) threadRun();
		var h: Future[int] = future(this) threadRun();
		var i: int = a.force();
		var j: int = b.force();
		var k: int = c.force();
		var l: int = d.force();
		var m: int = e.force();
		var n: int = f.force();
		var o: int = g.force();
		var p: int = h.force();
		var t1: int;
		var t2: int;
		atomic t1 = cnt;
		atomic t2 = cnt_broken;
		System.out.println("Atomic1: " + t1 + " =?= " + t2);
		return t1 == 8*N && t1 >= t2;
	}

	public static def main(var args: Rail[String]): void = {
		new Atomic1().execute();
	}
}
