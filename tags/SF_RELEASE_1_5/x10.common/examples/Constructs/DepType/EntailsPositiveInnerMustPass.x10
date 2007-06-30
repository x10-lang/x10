/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the type Test(:self.j==1) is a subtype of Test(:self.j==j) when j is of type int(:self==1).
 *
 * @author vj
 */
public class EntailsPositiveInnerMustPass extends x10Test {
    class Test(int i, int j) {
	public Test(:i==ii&&j==jj)(final int ii, final int jj) { property(ii,jj);}
    }

	public boolean run() {
	    final int(:self==1) j = 1;
	    Test(:self.j==j) x = new Test(1,1); 
	    return true;
	}
	public static void main(String[] args) {
		new EntailsPositiveInnerMustPass().execute();
	}
}

