/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A class with parameters, Test, is defined as an inner class. Check
 * that a type Tes(:i==j) can be defined. 
 *
 * @author vj
 */
public class EntailsPositiveInner_MustFailCompile extends x10Test {
    class Test(int i, int j) {
	public Test(:i==ii&&j==jj)(final int ii, final int jj) { property(ii,jj);}
    }

	public boolean run() {
	    Test(: i==j) x =  new Test(1,2); // should fail
	    return true;
	}
	public static void main(String[] args) {
		new EntailsPositiveInner_MustFailCompile().execute();
	}
}

