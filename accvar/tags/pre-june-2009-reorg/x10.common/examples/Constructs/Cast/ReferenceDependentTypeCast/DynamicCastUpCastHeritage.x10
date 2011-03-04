/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class DynamicCastUpCastHeritage extends x10Test {

	public DynamicCastUpCastHeritage() {
	}
	
	public boolean run(){
		final int a=1, b=2;

		// no constraints
		X10DepTypeClassOne one = new X10DepTypeSubClassOne(a,b);
		
		x10.lang.Object twoObj = new X10DepTypeSubClassOne(1,b);
		X10DepTypeClassOne(:p==1) two = (X10DepTypeClassOne(:p==1)) twoObj;
		
		x10.lang.Object threeObj = new X10DepTypeSubClassOne(a,b);
		X10DepTypeClassOne(:p==1) three = (X10DepTypeClassOne(:p==1)) threeObj

		x10.lang.Object fourObj = new X10DepTypeSubClassOne(b,b);
		X10DepTypeClassOne(:p==b) four = (X10DepTypeClassOne(:p==b)) fourObj;
		
		x10.lang.Object fiveObj = new X10DepTypeSubClassOne(a,b);
		X10DepTypeClassOne(:p==a) five = (X10DepTypeClassOne(:p==a)) fiveObj;

		return true;
	}

	public static void main(String [] args ) {
		new DynamicCastUpCastHeritage().execute();
	}
}

