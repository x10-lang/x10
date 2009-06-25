/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: Checks overflow is corretly handled by the constraint.
 * Note: We assign a value greater than short max value. 
 *       Then we check constraint's value has been overflowed.
 * @author vcave
 **/
public class PrimitiveDepTypeCast_Overflow1 extends x10Test {
	 private const aboveShort: int = (Short.MAX_VALUE to int) + 10;
	 
	public def run(): boolean = {
		// 32777 stored in a short is overflowed to -32759
		val overflow: short = aboveShort to short;

		var ss: short{self==overflow} = overflow to short{self==overflow};
		var sss: short{self==32777} = overflow to short{self==32777};
		var ssss: short{self==32777} = 32777 to short{self==32777};

		return (ss == -32759) && (sss == -32759) && (ssss == -32759);
	}

	public static def main(var args: Rail[String]): void = {
		new PrimitiveDepTypeCast_Overflow1().execute();
	}

}
