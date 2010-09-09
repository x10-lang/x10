/**
 * @author igor, 2/2006
 *
 * Returns from all branches of a "when" should work.
 */
public class WhenReturnAll {
	int test() {
		int ret = 0;
		when (X.t()) {
			return 1;
		} or (X.t()) {
			return 2;
		}
	}

	public boolean run() {
		int x = test();
		return true;
	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new WhenReturnAll()).run();
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

class X {
	static boolean t() { return true; }
}

