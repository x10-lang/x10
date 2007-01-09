/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks assignment of primitive to constrained primitives
 *          that may produce an overflow behave as expected.
 * @author vcave
 **/
public class PrimitiveDepTypeAssignmentOverflow extends x10Test {
	 private final long overIntMax = ((long) java.lang.Integer.MAX_VALUE) + 10000;
	 private final int overIntMaxAsInt = (int) overIntMax;

	 
	 public boolean run() {
		 System.out.println("long " + overIntMax);
		 System.out.println("long as int " + (int) overIntMax);
		
		 boolean res = false;
		 
		int(:self==overIntMaxAsInt) l1 = (int(:self==overIntMaxAsInt)) overIntMax;
		
		int iNeg = -2147473649;

		int(:self==-2147473649) i0 = (int(:self==-2147473649)) -2147473649;
		res &= (i0 == overIntMaxAsInt);
		
		int(:self==-2147473649) i1 = (int(:self==-2147473649)) iNeg;
		res = (i1 == overIntMaxAsInt);
		
		// The constraint on self is a long value converted to int
		// it makes that results in an overflow
		int(:self==overIntMax) i2 = (int(:self==overIntMax)) -2147473649;
		res = (i2 == overIntMaxAsInt);

		int(:self==overIntMax) i3 = (int(:self==overIntMax)) overIntMax;
		res &= (i3 == overIntMaxAsInt);
		
		// conversion from int to long fails compile
		// int(:self==2L) i4 = (int(:self==2L)) 2;
		// res &= (i4 == 2);
		
		// constraint on self is a long value converted to int but isn't overflowed
		int(:self==2L) i5 = (int(:self==2L)) 2L;
		res &= (i5 == 2);

		return res;
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeAssignmentOverflow().execute();
	}
}
 

 