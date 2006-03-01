/**
 * Check that a nullable int[] can be recognized by the compiler.
 *
 * @author igor Added 02/22/2006
 */
public class NullableIntArray {

	public boolean run() {
		final nullable int[] A = { 3,2,1 };
		int v = (A[1] == 2) ? 1 : 0;
		return v == 1;
	}

	public static void main(String[] args) {
		final boxedBoolean b = new boxedBoolean();
		try {
			finish b.val = new NullableIntArray().run();
		} catch (Throwable e) {
			e.printStackTrace();
			b.val = false;
		}
		System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b.val?0:1);
	}

	static class boxedBoolean {
		boolean val=false;
	}
}

