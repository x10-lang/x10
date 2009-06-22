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

	public def run(): boolean = {
		val sconstraint: short{self==0} = 0;
		
		// UPDATE COMMENT WHEN FIXED
		// not valid: because constraint constant and assigned one have different type
		var s0: short{self == 0} = 0 as short{self==0};

		// UPDATE COMMENT WHEN FIXED
		// not valid: as constraint on self is a short and the other is an integer
		var s1: short{self == sconstraint} = 0 as short{self==0};

		// UPDATE COMMENT WHEN FIXED
		// not valid: because cannot cast 0 which is of type int(:self==int) as short(:self==short)
		var s2: short{self == sconstraint} = 0 as short{self==sconstraint};
		
		// UPDATE COMMENT WHEN FIXED
		// not valid: as constraint on self is a short (self==sconstraint) and the other is an integer (self==0)
		var s3: short{self == sconstraint} = (0 as short) as short{self==0};

		// valid
		var s4: short{self == sconstraint} = (0 as short) as short{self==sconstraint};

		var j: short = -1;
		var i: short{self == sconstraint} = (++j) as short{self == sconstraint};
		return ((j==sconstraint) && (i==sconstraint));

	}

	public static def main(var args: Rail[String]): void = {
		new Short_ConstraintDeclaredAsShort().execute();
	}

}
