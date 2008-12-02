/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that field accesses within a depclause are checked to be final.
 *
 * @author vj
 */
public class NonFinalVariable_MustFailCompile extends x10Test {
	class Test(int i) {
	   public Test(:self.i==ii)(final int ii) {
	     property(ii);
	   }
	}
	
	public boolean run() {
	int ii = 52;
	   Test(:i==ii)  a =  new Test(52);
	    return true;
	}
	public static void main(String[] args) {
		new NonFinalVariable_MustFailCompile().execute();
	}
}

