/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Illustrates various scenario where constraints may causes problems with short.
 * Note: Contraint value is stored as an integer.
 * @author vcave
 **/
public class Short_ConstraintDeclaredAsShort extends x10Test {

	public boolean run() {
		final short(:self==0) sconstraint = 0;
		
		// UPDATE COMMENT WHEN FIXED
		// not valid: because constraint constant and assigned one have different type
		short (:self == 0) s0 = (short(:self==0)) 0;

		// UPDATE COMMENT WHEN FIXED
		// not valid: as constraint on self is a short and the other is an integer
		short (:self == sconstraint) s1 = (short(:self==0)) 0;

		// UPDATE COMMENT WHEN FIXED
		// not valid: because cannot cast 0 which is of type int(:self==int) to short(:self==short)
		short (:self == sconstraint) s2 = (short(:self==sconstraint)) 0;
		
		// UPDATE COMMENT WHEN FIXED
		// not valid: as constraint on self is a short (self==sconstraint) and the other is an integer (self==0)
		short (:self == sconstraint) s3 = (short(:self==0)) ((short) 0);

		// valid
		short (:self == sconstraint) s4 = (short(:self==sconstraint)) ((short) 0);

		short j = -1;
		short(:self == sconstraint) i = 
			(short (:self == sconstraint)) (++j);
		return ((j==sconstraint) && (i==sconstraint));

	}

	public static void main(String[] args) {
		new Short_ConstraintDeclaredAsShort().execute();
	}

}
 