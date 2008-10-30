/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks both inlined and "side effect aware" cast checking code
 *          works correctly
 * Note: This test try instanceof on several local objects and method call returned one.
 *       It also checks that method call (which may have side effects) are only called once per invocation.
 * @author vcave
 **/
public class InstanceofDownCast extends x10Test {
	 // method calls invocation counters
	 private int counter1 = 0;
	 private int counter2 = 0;
	 private int counter3 = 0;
	 
	public boolean run() {
		x10.lang.Object upcast1 = new X10DepTypeClassOne(1);
		x10.lang.Object upcast2 = new X10DepTypeSubClassOne(1,2);
		X10DepTypeClassOne upcast3 = new X10DepTypeSubClassOne(2,3);

		// instance of checked against an object
		boolean res1 = upcast1 instanceof X10DepTypeClassOne(:p==1);
		
		boolean res2 = upcast2 instanceof X10DepTypeClassOne(:p==1);
		boolean res3 = upcast2 instanceof X10DepTypeSubClassOne(:p==1&&a==2);

		boolean res4 = upcast3 instanceof X10DepTypeClassOne(:p==2);
		boolean res5 = upcast3 instanceof X10DepTypeSubClassOne(:p==2&&a==3);

		// instance of checked against a method return
		boolean res6 = this.getX10DepTypeClassOne(1) instanceof X10DepTypeClassOne(:p==1);
		
		boolean res7 = this.getX10DepTypeSubClassOne(1,2) instanceof X10DepTypeClassOne(:p==1);
		boolean res8 = this.getX10DepTypeSubClassOne(1,2) instanceof X10DepTypeSubClassOne(:p==1&&a==2);

		boolean res9 = this.getX10DepTypeSubClassOne_2(2,3) instanceof X10DepTypeClassOne(:p==2);
		boolean res10 = this.getX10DepTypeSubClassOne_2(2,3) instanceof X10DepTypeSubClassOne(:p==2&&a==3);

		
		return (res1 && res2 && res3 && res4 && res5 && 
				res6 && res7 && res8 && res9 && res10 &&
				// checks methods have been called only once per invocation
				// by the dynamic checking code.
				(counter1==1) && (counter2==2) && (counter3==2));
	}
	
	private x10.lang.Object getX10DepTypeClassOne(int c1) {
		counter1++;
		return new X10DepTypeClassOne(c1);
	}

	private x10.lang.Object getX10DepTypeSubClassOne(int c1, int c2) {
		counter2++;
		return new X10DepTypeSubClassOne(c1, c2);
	}

	private X10DepTypeClassOne getX10DepTypeSubClassOne_2(int c1, int c2) {
		counter3++;
		return new X10DepTypeSubClassOne(c1, c2);
	}

	public static void main(String[] args) {
		new InstanceofDownCast().execute();
	}

}
 
