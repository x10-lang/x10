import harness.x10Test;

/**
 * Purpose: Checks an overflow is statically detected when a constant is to assign.
 * Issue: At compile time we can infer the integer constant has been overflowed,
  *       and throw an exception as constraint is not meet.
 * @author vcave
 **/
public class Long_ConstraintDeclaredAsInteger_OverFlow_MustFailCompile extends x10Test {

	 public boolean run() {
		boolean result = false;
		try {
		// this time constraint is a long but value to assign is an overflowed integer
		// Hence at compile time we can state contraint value is != from constant.
		long(:self==2147493647L) l3 = (long(:self==2147493647L)) 2147493647;
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new  Long_ConstraintDeclaredAsInteger_OverFlow_MustFailCompile().execute();
	}

}
 