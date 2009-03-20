/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks casted assignment to variable works.
 * Note: We don't need to generates special runtime checking for constraints are types are known statically.
 * @author vcave
 **/
public class ClassDepTypeCastWideningClause extends x10Test {

	public boolean run() {
		// identity cast should not generate additionnal runtime checking code
		X10DepTypeClassTwo(:p==0&&q==1) test1 = 
			((X10DepTypeClassTwo(:p==0&&q==1)) new X10DepTypeClassTwo(0,1));

	        // widening cast can be checked statically
		X10DepTypeClassTwo(:p==0) test2 = 
			((X10DepTypeClassTwo(:p==0)) new X10DepTypeClassTwo(0,1));
		
		X10DepTypeClassTwo(:q==1) test3 = 
			((X10DepTypeClassTwo(:q==1)) new X10DepTypeClassTwo(0,1));
		
		X10DepTypeClassTwo test4 = 
			((X10DepTypeClassTwo) new X10DepTypeClassTwo(0,1));
		return true;
	}

	public static void main(String[] args) {
		new ClassDepTypeCastWideningClause().execute();
	}
}
 