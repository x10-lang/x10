/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * Issue: Constant to assign does not meet constraint requirement of target type.
 * @author vcave
 **/
public class AssignmentIntLitteralToConstrainedInt_MustFailCompile extends x10Test {

	public def run(): boolean = {
		
		try { 
			var j: int{self == 0} = 1;
		}catch (e: Throwable) {
			return false;
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentIntLitteralToConstrainedInt_MustFailCompile().execute();
	}

}
