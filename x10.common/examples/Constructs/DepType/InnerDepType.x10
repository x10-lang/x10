/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that property syntax is accepted.
 *
 * @author vj
 */
public class InnerDepType extends x10Test {
    class Test(int i) {
       public Test(:self.i==i)(final int i) { property(i); }
    }
	public boolean run() {
	 
	    Test(: self.i==0) x =   new Test(0); 
	    return true;
	}
	public static void main(String[] args) {
		new InnerDepType().execute();
	}
}

