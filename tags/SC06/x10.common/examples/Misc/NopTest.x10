import harness.x10Test;

/**
 * Test that does nothing.  Useful to test the testing
 * harness.
 */
public class NopTest extends x10Test {

	public boolean run() {
		return true;
	}

	public static void main(String[] args) {
		new NopTest().execute();
	}
}

