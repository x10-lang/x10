/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks if creation and force of Future in different activities work.
 *
 * @author Christoph von Praun, 5/2005
 * @author kemal, 5/2005
 */
public class FutureTest5 extends x10Test {

	nullable<future<int>> fut;

	/**
	 * Create a future in one activity, and then
	 * force it in a different activity.
	 */
	public boolean run() {
		return
			testUp_(false) &&
			testUp_(true) &&
			testDown_() &&
			testSibling_(false) &&
			testSibling_(true);
	}

	/**
	 * Create future in child, force it in parent.
	 */
	private boolean testUp_(final boolean del) {
		atomic fut = null;
		async (here) {
			future<int> t1 = future (here) { 42 } ;
			atomic fut = t1;
			if (del)
				x10.lang.Runtime.sleep(500);
		};
		nullable<future<int>> t2;
		when (fut != null) { t2 = fut; }
		int fortytwo = t2.force();
		System.out.println("up done");
		return fortytwo == 42;
	}

	/**
	 * Create future in parent, force it in child.
	 */
	private boolean testDown_() {
		final future<int> fut_l = future (here) { 42 } ;
		finish async (here) {
			int fortytwo = fut_l.force();
			System.out.println("down done");
		};
		return true;
	}

	/**
	 * Create future in child 1, force it in child 2.
	 */
	private boolean testSibling_(final boolean del) {
		atomic fut = null;
		async (here) {
			future<int> t1 = future (here) { 42 } ;
			atomic fut = t1;
			if (del)
				x10.lang.Runtime.sleep(500);
		}
		finish async (here) {
			nullable<future<int>> t2;
			when (fut != null) { t2 = fut; }
			int fortytwo = t2.force();
			System.out.println("sibling done");
		};
		return true;
	}

	public static void main(String[] args) {
		new FutureTest5().execute();
	}
}

