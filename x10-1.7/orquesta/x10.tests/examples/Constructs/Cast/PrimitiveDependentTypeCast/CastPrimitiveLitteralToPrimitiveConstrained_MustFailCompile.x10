/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks dependent type constraint information are propagated along with the variable.
 * Issue: Constant to promotedoes not meet constraint of targeted type.
 * @author vcave
 **/
public class CastPrimitiveLitteralToPrimitiveConstrained_MustFailCompile extends x10Test {

	public def run(): boolean {
		
		try { 
                        val j: int{self==0} = 1 as int{self==0};
		}catch(e: Throwable) {
			return false;
		}

		return true;
	}

	public static def main(args: Rail[String]): void {
		new CastPrimitiveLitteralToPrimitiveConstrained_MustFailCompile().execute();
	}

}
 
