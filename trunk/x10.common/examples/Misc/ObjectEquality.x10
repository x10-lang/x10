/**
 * Comparing objects should not be rewritten to boxed calls.
 * Distilled from the old CompilerNullPointerException test.
 *
 * @author Igor Peshansky
 */
public class ObjectEquality {
	nullable java.lang.Object objField;

	public boolean run() {
		final java.lang.Object obj = new java.lang.Object();
		if (obj == objField)
			return false;
		return true;

	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new ObjectEquality()).run();
		} catch (Throwable e) {
			e.printStackTrace();
			b.val=false;
		}
		System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b.val?0:1);
	}

	static class boxedBoolean {
		boolean val=false;
	}
}

