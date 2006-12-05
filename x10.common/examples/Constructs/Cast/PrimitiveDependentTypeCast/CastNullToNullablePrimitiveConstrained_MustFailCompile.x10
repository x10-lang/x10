import harness.x10Test;

/**
 * Purpose:
 * Issue:
 * @author vcave
 **/
 public class CastNullToNullablePrimitiveConstrained_MustFailCompile extends x10Test {

	public boolean run() {
		nullable<int(:self==3)> i = (nullable<int(:self==3)>) null;
		return false;
	}

	public static void main(String[] args) {
		new CastNullToNullablePrimitiveConstrained_MustFailCompile().execute();
	}
}