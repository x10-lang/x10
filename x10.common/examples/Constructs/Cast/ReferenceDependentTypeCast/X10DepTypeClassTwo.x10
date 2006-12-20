/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks variable name shadowing works correctly.
 * @author vcave
 **/
public class X10DepTypeClassTwo(int p, int q) extends x10Test {

	public X10DepTypeClassTwo(:self.p==a&&self.q==b)(final int a, final int b) {
		property(a,b);
	}
	    
	public boolean run() {
		
		X10DepTypeClassTwo(:self.p==this.p) one = 
			 new X10DepTypeClassTwo(this.p,0);
		
		return one.p() == 0;
	}
	
	public static void main(String[] args) {
		new X10DepTypeClassTwo(0,0).execute();
	}
	
}

