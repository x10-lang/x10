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
public class ClockTest10a extends x10Test {
	// varX[0] and varX[1] serve alternately as
	// the "new result" and "old result"
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
			System.out.println(k + " A producing " + varA[ph(k)]);
			next;
		}
	}
	void taskB(final clock a, final clock b) {
		for (point [k]: [1:N]) {
			varB[ph(k)] = varA[ph(k-1)]+varA[ph(k-1)];
			System.out.println(k + " B consuming oldA producing " + varB[ph(k)]);
			next;
		}
	}
	void taskC(final clock a, final clock c) {
		for (point [k]: [1:N]) {
			varC[ph(k)] = varA[ph(k-1)]*varA[ph(k-1)];
			System.out.println(k+" C consuming oldA producing "+ varC[ph(k)]);
			next;
		}
	}
	void taskD(final clock b, final clock c) {
		for (point [k]: [1:N]) {
			varD[ph(k)] = varB[ph(k-1)]+varC[ph(k-1)]+10;
			System.out.println(k+" D consuming oldC producing "+varD[ph(k)]);
			int n = k-pipeDepth;
			chk(!(k>pipeDepth) || varD[ph(k)] == n+n+n*n+10);
			next;
		}
	}
	void taskE(final clock c) {
		for (point [k]: [1:N]) {
			varE[ph(k)] = varC[ph(k-1)]*7;
			System.out.println(k+" E consuming oldC producing "+varE[ph(k)]);
			int n = k-pipeDepth;
			chk(!(k>pipeDepth) || varE[ph(k)] == n*n*7);
			next;
		}
	}

	public static void main(String[] args) {
		new ClockTest10a().execute();
	}

	static class boxedInt {
		int val;
	}
}

