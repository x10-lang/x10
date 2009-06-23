/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Check primitive dependent type assignment to primitive variable works.
 * @author vcave
 **/
public class AssignmentPrimitiveConstrainedToPrimitive extends x10Test {

	public def run(): boolean = {
		
		try { 
			var i: int{self == 0} = 0;
			var k: int{self == 1} = 1;
			var j: int = 0;
			j = i;
			j = k;
		}catch(e: Throwable) {
			return false;
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentPrimitiveConstrainedToPrimitive().execute();
	}

}
