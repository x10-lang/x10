/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Some updates of cnt_broken may be lost,
 * since the read and write are not
 * inside the same atomic section.
 */
public class Atomic1 extends x10Test {

	int cnt = 0;
	int cnt_broken = 0;
	const int N = 100;
	int threadRun() {
		for (int i = 0; i < N; ++i) {
			int t;
			atomic t = cnt_broken;
			atomic ++cnt;
			atomic cnt_broken = t + 1;
		}
		return 0;
	}

	public boolean run() {
		future<int> a = future(this) { threadRun() };
		future<int> b = future(this) { threadRun() };
		future<int> c = future(this) { threadRun() };
		future<int> d = future(this) { threadRun() };
		future<int> e = future(this) { threadRun() };
		future<int> f = future(this) { threadRun() };
		future<int> g = future(this) { threadRun() };
		future<int> h = future(this) { threadRun() };
		int i = a.force();
		int j = b.force();
		int k = c.force();
		int l = d.force();
		int m = e.force();
		int n = f.force();
		int o = g.force();
		int p = h.force();
		int t1;
		int t2;
		atomic t1 = cnt;
		atomic t2 = cnt_broken;
		System.out.println("Atomic1: " + t1 + " =?= " + t2);
		return t1 == 8*N && t1 >= t2;
	}

	public static void main(String[] args) {
		new Atomic1().execute();
	}
}

