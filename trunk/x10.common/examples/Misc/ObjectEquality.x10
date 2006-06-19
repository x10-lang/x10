import harness.x10Test;

/**
 * Comparing objects should not be rewritten to boxed calls.
 * Distilled from the old CompilerNullPointerException test.
 *
 * @author Igor Peshansky
 */
public class ObjectEquality extends x10Test {

	nullable java.lang.Object objField;

	public boolean run() {
		final java.lang.Object obj = new java.lang.Object();
		if (obj == objField)
			return false;
		return true;
	}

	public static void main(String[] args) {
		new ObjectEquality().execute();
	}
}

