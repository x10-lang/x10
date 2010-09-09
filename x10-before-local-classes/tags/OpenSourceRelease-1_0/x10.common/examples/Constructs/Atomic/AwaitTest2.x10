import harness.x10Test;

/**
 * Minimal test for await.
 */
public class AwaitTest2 extends x10Test {

	int val = 42;

	public boolean run() {
		await(val == 42);
		return true;
	}

	public static void main(String[] args) {
		new AwaitTest2().execute();
	}
}

