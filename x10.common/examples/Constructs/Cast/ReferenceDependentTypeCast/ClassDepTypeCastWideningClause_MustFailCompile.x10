/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks cast to a wider clause that does not meet constraints fails compile.
 * X10DepTypeClassTwo(:p==1) <-- X10DepTypeClassTwo(:p==0&&q==1)
 * @author vcave
 **/
public class ClassDepTypeCastWideningClause_MustFailCompile extends x10Test {

	public boolean run() {						
		X10DepTypeClassTwo(:p==1) test = 
			((X10DepTypeClassTwo(:p==1)) new X10DepTypeClassTwo(0,1));						
		return true;
	}

	public static void main(String[] args) {
		new ClassDepTypeCastWideningClause_MustFailCompile().execute();
	}
}
 