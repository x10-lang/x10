import harness.x10Test;

/**
 * Testing future of x10.compilergenerated.BoxedInteger.
 * This should not be visible to user.
 */
public class Future1Boxed extends x10Test {
	public boolean run() {
		future<x10.compilergenerated.BoxedInteger> x = future (here) { new x10.compilergenerated.BoxedInteger(42) };
		return (x.force()).intValue() == 42;
	}

	public static void main(String[] args) {
		new Future1Boxed().execute();
	}
}

