/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks assignment of primitive to constrained primitives.
 * @author vcave
 **/
public class PrimitiveDepTypeAssignmentOverflow extends x10Test {
	 private final long overIntMax = ((long) java.lang.Integer.MAX_VALUE) + 10000;
	 private final int overIntMaxAsInt = (int) overIntMax;

	 
	 public boolean run() {
		 System.out.println("long " + overIntMax);
		 System.out.println("int " + (int) overIntMax);
		 final int overIntMaxAsInt = (int) overIntMax;
		int(:self==overIntMaxAsInt) l1 = (int(:self==overIntMaxAsInt)) overIntMax;
		int(:self==-2147473649) l3 = (int(:self==-2147473649))  overIntMax;
		

		// try {
		// don't work because 2147493647 is implicity an int
		// long(:self==2147493647L) l3 = (long(:self==2147493647L)) 2147493647;
		// } catch (ClassCastException e) {
			// return true;
		// }
		return true;
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeAssignmentOverflow().execute();
	}
}
 

 