/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that only properties can be accessed via self.
 *
 * @author vj
 */
public class SelfFieldsMustBeProperties1_MustFailCompile extends x10Test {
	class Test(int i) {
	   public final boolean bad; // not declared as a property.
	   public Test(int ii) {
	     i = ii;
	     bad = true;
	   }
	}
	
	public boolean run() {
	   Test (:i==52) a = (Test(:i==52 && self.bad==true)) new Test(52);
	    return true;
	}
	public static void main(String[] args) {
		new SelfFieldsMustBeProperties1_MustFailCompile().execute();
	}
}

