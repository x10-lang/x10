/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Using clocks to do simple producer consumer synchronization
 * for this task DAG (arrows point downward)
 * in pipelined fashion. On each clock period,
 * each stage of the pipeline reads the previous clock period's
 * result from the previous stage and produces its new result
 * for the current clock period.
 *
 * <code>
 *   A   stage 0 A produces the stream 1,2,3,...
 *  / \
 *  B  C  stage 1 B is "double", C is "square" function
 *  \ /|
 *   D E  stage 2 D is \(x,y)(x+y+10), E is \(x)(x*7)
 * </code>
 *
 * @author kemal 4/2005
 */
public class ClockTest10a extends x10Test {
	// varX[0] and varX[1] serve alternately as
	// the "new result" and "old result"
	var varA: Array[int] = Array.make[int](2, (x:point)=>0);
	var varB: Array[int] = Array.make[int](2, (x:point)=>0);
	var varC: Array[int] = Array.make[int](2, (x:point)=>0);
	var varD: Array[int] = Array.make[int](2, (x:point)=>0);
	var varE: Array[int] = Array.make[int](2, (x:point)=>0);
	public const N = 10;
	public const pipeDepth = 2;

	static def ph(var x: int): int = { return x % 2; }

	public def run(): boolean = {
		finish async(here) {
			val a: clock = clock.factory.clock();
			val b: clock = clock.factory.clock();
			val c: clock = clock.factory.clock();
			async clocked(a) taskA(a);
			async clocked(a, b) taskB(a, b);
			async clocked(a, c) taskC(a, c);
			async clocked(b, c) taskD(b, c);
			async clocked(c) taskE(c);
		}
		return true;
	}

	def taskA(val a: clock): void = {
		for (val k in [1..N]) {
			varA(ph(k)) = k;
			System.out.println(k + " A producing " + varA(ph(k)));
			next;
		}
	}
	def taskB(val a: clock, val b: clock): void = {
		for (val k in [1..N]) {
			varB(ph(k)) = varA(ph(k-1))+varA(ph(k-1));
			System.out.println(k + " B consuming oldA producing " + varB(ph(k)));
			next;
		}
	}
	def taskC(val a: clock, val c: clock): void = {
		for (val k in [1..N]) {
			varC(ph(k)) = varA(ph(k-1))*varA(ph(k-1));
			System.out.println(k+" C consuming oldA producing "+ varC(ph(k)));
			next;
		}
	}
	def taskD(val b: clock, val c: clock): void = {
		for (val k in [1..N]) {
			varD(ph(k)) = varB(ph(k-1))+varC(ph(k-1))+10;
			System.out.println(k+" D consuming oldC producing "+varD(ph(k)));
			var n: int = k-pipeDepth;
			chk(!(k>pipeDepth) || varD(ph(k)) == n+n+n*n+10);
			next;
		}
	}
	def taskE(val c: clock): void = {
		for (val k in [1..N]) {
			varE(ph(k)) = varC(ph(k-1))*7;
			System.out.println(k+" E consuming oldC producing "+varE(ph(k)));
			var n: int = k-pipeDepth;
			chk(!(k>pipeDepth) || varE(ph(k)) == n*n*7);
			next;
		}
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest10a().execute();
	}

	static class boxedInt {
		var val: int;
	}
}
