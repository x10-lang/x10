import harness.x10Test;

/**
 * A nullable future test.
 */
public class FutureNullable1Boxed extends x10Test {
	public boolean run() {
		final boolean b = X.t();
		future<nullable future<FutureNullable1Boxed>> x =
			future {
				b ? (( nullable future<FutureNullable1Boxed> ) null)
				  : (( nullable future<FutureNullable1Boxed> ) future { new FutureNullable1Boxed() } )
			};
		return x.force() == null;
	}

	public static void main(String[] args) {
		new FutureNullable1Boxed().execute();
	}

	// class to make optimizations more difficult
	static class X {
		public static boolean t() { return true; }
	}
}

