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
 * Issue: assignment is illegal as constraint is not meet.
 * @author vcave
 **/
public class AssignmentLitteralPrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {

	public def run(): boolean = {
		
		try { 
			var i: int{self == 0} = 0;
			i = 1;
		}catch(e: Throwable) {
			return false;
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentLitteralPrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}

}
