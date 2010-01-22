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
 * Issue: Value to cast does not meet constraint requirement of target type.
 * @author vcave
 **/
public class AssignmentPrimitiveConstrainedIdentity extends x10Test {

	public def run(): boolean = {
		
		try { 
         var i: int(0) = 0;
         var j: int(0) = 0;
         return i == j;
		}catch(e: Throwable) {
			return false;
		}

      
	}

   public static def main(Rail[String]) {
		new AssignmentPrimitiveConstrainedIdentity().execute();
	}

}
