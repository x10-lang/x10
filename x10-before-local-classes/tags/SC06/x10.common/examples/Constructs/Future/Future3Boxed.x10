import harness.x10Test;

/**
 * A future test.
 */
public class Future3Boxed extends x10Test {
	public boolean run() {
		future<x10.compilergenerated.BoxedInteger> x = future { new x10.compilergenerated.BoxedInteger(42) };
		return (x.force()).intValue() == 42;
	}

	public static void main(String[] args) {
		new Future3Boxed().execute();
	}
}

