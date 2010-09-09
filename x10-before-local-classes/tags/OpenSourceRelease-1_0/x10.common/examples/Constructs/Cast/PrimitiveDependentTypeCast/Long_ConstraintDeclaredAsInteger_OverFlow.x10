import harness.x10Test;

/**
 * Purpose: Shows a constraint value may be overflowed.
 * Issue: Declared constraint is an overflowed integer, which makes assignment fail at runtime.
 * @author vcave
 **/
public class Long_ConstraintDeclaredAsInteger_OverFlow extends x10Test {

	 private final long overIntMax = ((long) java.lang.Integer.MAX_VALUE) + 10000;
	 
	 public boolean run() {
		try {
			// don't work because 2147493647 is implicitly an int and outboud
			// integer capacity. Hence assigning the same value as a long fail.
			long(:self==2147493647) l2 = (long(:self==2147493647)) overIntMax;
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new  Long_ConstraintDeclaredAsInteger_OverFlow().execute();
	}

}
 