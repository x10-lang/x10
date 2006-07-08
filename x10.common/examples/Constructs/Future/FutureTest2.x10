import harness.x10Test;

/**
 * Minimal test for future.
 */
public class FutureTest2 extends x10Test {

	public boolean run() {
		future<boolean> ret = future (here) { this.m() };
		boolean syn = ret.force();
		return syn;
	}

	boolean m() {
		return true;
	}

	public static void main(String[] args) {
		new FutureTest2().execute();
	}
}

