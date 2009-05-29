/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks an object returned by a method is correctly 
 * checked against target type constraints. 
 * This test is particularly focused on using variable in constraints
 * Note: The following code will use java reflexion to dynamically checks constraints.
 * @author vcave
 **/
public class DynamicCast6_DepTypeVariables extends x10Test {

	public boolean run() {
		try {
			final int a = 1;
			X10DepTypeClassTwo(:p==a&&q==2) convertedObject =
				(X10DepTypeClassTwo(:p==a&&q==2)) this.objectReturner1();

			X10DepTypeClassTwo(:p==a&&q==p) convertedObject2 =
				(X10DepTypeClassTwo(:p==a&&q==p)) this.objectReturner2();

			X10DepTypeClassTwo(:p==a) convertedObject3 =
				(X10DepTypeClassTwo(:p==a&&q==p)) this.objectReturner2();

			X10DepTypeClassTwo(:a==p) convertedObject4 =
				(X10DepTypeClassTwo(:p==a&&q==p)) this.objectReturner2();

			X10DepTypeClassTwo(:a==p) convertedObject5 =
				(X10DepTypeClassTwo(:p==a&&q==p)) this.objectReturner3();
		}catch(ClassCastException e) {
			return false;
		}
		
		return true;
	}
	
	public x10.lang.Object objectReturner1() {
		return new X10DepTypeClassTwo(1,2);
	}
	
	public x10.lang.Object objectReturner2() {
		return new X10DepTypeClassTwo(1,1);
	}
	
	public x10.lang.Object objectReturner3() {
		final int b = 1;
		return new X10DepTypeClassTwo(b,b);
	}
	
	public static void main(String[] args) {
		new DynamicCast6_DepTypeVariables().execute();
	}

}
 