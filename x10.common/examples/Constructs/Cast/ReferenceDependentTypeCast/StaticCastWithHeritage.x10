/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class StaticCastWithHeritage extends x10Test {

	public boolean run(){
		final int(:self==1) a=1; 
		final int(:self==2) b=2;

		// no constraints
		X10DepTypeClassOne one = new X10DepTypeSubClassOne(a,b);
		
		X10DepTypeClassOne(:p==1) two = (X10DepTypeClassOne(:p==1)) new X10DepTypeSubClassOne(1,b);
		
		X10DepTypeClassOne(:p==1) three = (X10DepTypeClassOne(:p==1)) new X10DepTypeSubClassOne(a,b);

		X10DepTypeClassOne(:p==b) four = (X10DepTypeClassOne(:p==b)) new X10DepTypeSubClassOne(b,b);
		
		X10DepTypeClassOne(:p==a) five = (X10DepTypeClassOne(:p==a)) new X10DepTypeSubClassOne(a,b);
		
		return true;
	}

	public static void main(String [] args ) {
		new StaticCastWithHeritage().execute();
	}
}

