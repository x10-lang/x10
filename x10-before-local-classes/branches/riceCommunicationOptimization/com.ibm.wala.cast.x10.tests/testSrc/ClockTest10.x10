//OPTIONS: -PLUGINS=x10.klock.plugin.KlockPlugin
//CLASSPATH: ../classes/klock.jar
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */


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
public class ClockTest10  {
	int[] varA = new int[2];
	int[] varB = new int[2];
	int[] varC = new int[2];
	int[] varD = new int[2];
	int[] varE = new int[2];
	const int N = 10;
	const int pipeDepth = 2;

	static int ph(int x) { return x % 2; }

	public boolean run() {
		finish async(here) {
			final clock a = clock.factory.clock();
			final clock b = clock.factory.clock();
			final clock c = clock.factory.clock();
			async clocked(a) taskA(a);
			async clocked(a, b) taskB(a, b);
			async clocked(a, c) taskC(a, c);
			async clocked(b, c) taskD(b, c);
			async clocked(c) taskE(c);
		}
		return true;
	}

	void taskA(final clock a) {
		for (point [k]: [1:N]) {
			varA[ph(k)] = k;
			System.out.println(Thread.currentThread() + " " + k + " A producing " + varA[ph(k)]);
			next;
		}
	}
	void taskB(final clock a, final clock b) {
		for (point [k]: [1:N]) {
			final boxedInt tmp = new boxedInt();
			finish tmp.val = varA[ph(k-1)]+varA[ph(k-1)];
			System.out.println(Thread.currentThread() + " " + k + " B consuming oldA producing " + tmp.val);
			a.resume();
			varB[ph(k)] = tmp.val;
			System.out.println(Thread.currentThread() + " " + "B before next");
			next;
		}
	}
	void taskC(final clock a, final clock c) {
		for (point [k]: [1:N]) {
			final boxedInt tmp = new boxedInt();
			finish tmp.val = varA[ph(k-1)]*varA[ph(k-1)];
			System.out.println(Thread.currentThread() + " " + k + " C consuming oldA "+ tmp.val);
			a.resume();
			varC[ph(k)] = tmp.val;
			System.out.println(Thread.currentThread() + " " + "C before next");
			next;
		}
	}
	void taskD(final clock b, final clock c) {
		for (point [k]: [1:N]) {
			final boxedInt tmp = new boxedInt();
			finish tmp.val = varB[ph(k-1)]+varC[ph(k-1)]+10;
			System.out.println(Thread.currentThread() + " " + k + " D consuming oldB+oldC producing " + tmp.val);
			c.resume();
			b.resume();
			varD[ph(k)] = tmp.val;
			System.out.println(Thread.currentThread() + " " + k + " D before next");
			int n = k-pipeDepth;
			//chk(!(k>pipeDepth) || varD[ph(k)] == n+n+n*n+10);
			next;
		}
	}
	void taskE(final clock c) {
		for (point [k]: [1:N]) {
			final boxedInt tmp = new boxedInt();
			finish tmp.val = varC[ph(k-1)]*7;
			System.out.println(Thread.currentThread() + " " + k + " E consuming oldC producing " + tmp.val);
			c.resume();
			varE[ph(k)] = tmp.val;
			System.out.println(Thread.currentThread() + " " + k + " E before next");
			int n = k-pipeDepth;
			//chk(!(k>pipeDepth) || varE[ph(k)] == n*n*7);
			next;
		}
	}

	public static void main(String[] args) {
		new ClockTest10().run();
	}

	static class boxedInt {
		int val;
	}
}

