/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

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
public class ClockTest10 extends x10Test {
    var varA = Array[int].make(2,(x:nat)=>0);
    var varB = Array[int].make(2,(x:nat)=>0);
    var varC = Array[int].make(2,(x:nat)=>0);
    var varD = Array[int].make(2,(x:nat)=>0);
    var varE = Array[int].make(2,(x:nat)=>0);
    public const N: int = 10;
    public const pipeDepth: int = 2;

    static def ph(var x: int): int = { return x % 2; }

    public def run(): boolean = {
	finish async(here) {
	    val a = new Clock();
	    val b = new Clock();
	    val c = new Clock();
	    async clocked(a) taskA(a);
	    async clocked(a, b) taskB(a, b);
	    async clocked(a, c) taskC(a, c);
	    async clocked(b, c) taskD(b, c);
	    async clocked(c) taskE(c);
	}
	return true;
    }

    def taskA(val a: clock): void = {
	for (val (k): point in [1..N]) {
	    varA(ph(k)) = k;
	    System.out.println(Thread.currentThread() + " " + k + " A producing " + varA(ph(k)));
	    next;
	}
    }
    def taskB(val a: clock, val b: clock): void = {
	for (val (k): point in [1..N]) {
	    val tmp = new boxedInt();
	    finish tmp.val = varA(ph(k-1))+varA(ph(k-1));
	    System.out.println(Thread.currentThread() + " " + k + " B consuming oldA producing " + tmp.val);
	    a.resume();
	    varB(ph(k)) = tmp.val;
	    System.out.println(Thread.currentThread() + " " + "B before next");
	    next;
	}
    }
    def taskC(val a: clock, val c: clock): void = {
	for (val (k): point in [1..N]) {
	    val tmp: boxedInt = new boxedInt();
	    finish tmp.val = varA(ph(k-1))*varA(ph(k-1));
	    System.out.println(Thread.currentThread() + " " + k + " C consuming oldA "+ tmp.val);
	    a.resume();
	    varC(ph(k)) = tmp.val;
	    System.out.println(Thread.currentThread() + " " + "C before next");
	    next;
	}
    }
    def taskD(val b: clock, val c: clock): void = {
	for (val (k): point in [1..N]) {
	    val tmp: boxedInt = new boxedInt();
	    finish tmp.val = varB(ph(k-1))+varC(ph(k-1))+10;
	    System.out.println(Thread.currentThread() + " " + k + " D consuming oldB+oldC producing " + tmp.val);
	    c.resume();
	    b.resume();
	    varD(ph(k)) = tmp.val;
	    System.out.println(Thread.currentThread() + " " + k + " D before next");
	    var n: int = k-pipeDepth;
	    chk(!(k>pipeDepth) || varD(ph(k)) == n+n+n*n+10);
	    next;
	}
    }
    def taskE(val c: clock): void = {
	for (val (k): point in [1..N]) {
	    val tmp: boxedInt = new boxedInt();
	    finish tmp.val = varC(ph(k-1))*7;
	    System.out.println(Thread.currentThread() + " " + k + " E consuming oldC producing " + tmp.val);
	    c.resume();
	    varE(ph(k)) = tmp.val;
	    System.out.println(Thread.currentThread() + " " + k + " E before next");
	    var n: int = k-pipeDepth;
	    chk(!(k>pipeDepth) || varE(ph(k)) == n*n*7);
	    next;
	}
    }

    public static def main(var args: Rail[String]): void = {
	new ClockTest10().execute();
    }

    static class boxedInt {
	var val: int;
    }
}
