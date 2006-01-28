/**
 * Should report the type mismatch in the comparison in the loop, and fail
 * to compile gracefully.
 *
 * @author Bin Xin (xinb@cs.purdue.edu)
 */
public class NullableComparison {

	(nullable java.lang.Object)[] objList;

	public boolean run() {
		final java.lang.Object obj = new java.lang.Object();
		int i = 5;
		while (i > 0 && (obj != objList[i])) {
			i--;
		}
		if (i > 0)
			return false;
		return true;

	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new NullableComparison()).run();
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

