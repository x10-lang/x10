/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that final local variables can be accessed in a depclause.
 *
 * @author vj
 */
public class VariableMustBeFinalInDepClause extends x10Test { 
	class Test(int i) {
		public Test(:i==ii)(final int ii) {
			property(ii);
		}
	}
	public Test(:i==52) m(Test(:i==52) t) {
		final int(:self==52) j = 52;
		 Test(:i==j)  a =  t;
		 return a;
	}
	public boolean run() {
		Test(:i==52) t = new Test(52);
	    return m(t).i==52;
	}
	public static void main(String[] args) {
		new VariableMustBeFinalInDepClause().execute();
	}
	
}

