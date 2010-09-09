import harness.x10Test;

/**
 * Purpose: Checks assignment of primitive to constrained primitives.
 * @author vcave
 **/
public class PrimitiveDepTypeAssignmentOverflow extends x10Test {
	 private final long overIntMax = ((long) java.lang.Integer.MAX_VALUE) + 10000;
	 
	 public boolean run() {
		// works because the constraint directly refer to the static field
		long(:self==overIntMax) l1 = (long(:self==overIntMax)) overIntMax;
			
		// don't work because 2147493647 is store in an int
		long(:self==2147493647) l2 = (long(:self==2147493647)) overIntMax;

		return true;
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeAssignmentOverflow().execute();
	}
}
 

 