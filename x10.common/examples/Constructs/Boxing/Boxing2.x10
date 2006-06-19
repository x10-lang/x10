import harness.x10Test;

/**
 * Tests interaction of parentheses and boxing
 */
public class Boxing2 extends x10Test {

	public boolean run() {
		String x = "The number is "+(X.five()*2);
		if (!x.equals("The number is 10")) return false;
		String y = "The number is "+(200+X.five()*2);
		if (!y.equals("The number is 210")) return false;
		return true;
	}

	public static void main(String[] args) {
		new Boxing2().execute();
	}

	static class X {
		public static int five() { return 5; }
	}
}

