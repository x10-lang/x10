import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastNullToReference_MustFailCompile extends x10Test {

	public boolean run() {
		x10.lang.Object obj = (x10.lang.Object) null;
		return false;
	}

	public static void main(String[] args) {
		new CastNullToReference_MustFailCompile().execute();
	}
}