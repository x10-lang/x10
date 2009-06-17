/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for the interaction of clocks and future.
 * clock.doNext should not wait for futures to
 * terminate.
 * vj: 07/08/06 Changed future<int@here> to future<int>. Value types can no longer have place annotations.
 * @author Christoph von Praun
 */
public class ClockFuture extends x10Test {

	private boolean clock_has_advanced;

	public int m() {
		int ret = 0;
		when (clock_has_advanced) {
			ret = 42;
		}
		return ret;
	}

	public boolean run() {
		final clock c = clock.factory.clock();
		future<int> f = future (here) { m() };
		System.out.print("1 ... ");
		// this next should not wait on the future
		next;
		System.out.print("2 ... ");
		atomic { clock_has_advanced = true; }
		System.out.print("3 ...");
		int result = f.force();
		chk(result == 42);
		System.out.println("4");
		return true;
	}

	public static void main(String[] args) {
		new ClockFuture().execute();
	}
}

