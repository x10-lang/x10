import harness.x10Test;

/**
 * A future test.
 */
public class Future3 extends x10Test {
	public boolean run() {
		future<int> x = future { 47 };
		return (x.force()) == 47;
	}

	public static void main(String[] args) {
		new Future3().execute();
	}
}

