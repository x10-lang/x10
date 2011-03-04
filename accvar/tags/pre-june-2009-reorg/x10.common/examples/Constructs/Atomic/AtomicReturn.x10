/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Atomic return test
 */
public class AtomicReturn extends x10Test {
	int a = 0;
	static final int N = 100;

	int update1() {
		atomic {
			a++;
			return a;
		}
	}

	int update3() {
		atomic {
			return a++;
		}
	}

	public boolean run() {
		update1();
		update3();
		System.out.println(a);
		return a == 2;
	}

	public static void main(String[] args) {
		new AtomicReturn().execute();
	}
}

