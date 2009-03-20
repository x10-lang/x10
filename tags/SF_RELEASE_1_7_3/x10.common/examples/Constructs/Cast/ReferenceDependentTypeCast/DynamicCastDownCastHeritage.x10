/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks bad constraint is detected while performing a
 * downcast from mother class type to sub class type .
 * @author vcave
 **/
public class DynamicCastDownCastHeritage extends x10Test {

	public DynamicCastDownCastHeritage() {
	}
	
	public boolean run(){
		try {
		final int a=1, b=2;

		x10.lang.Object toObj = new X10DepTypeSubClassOne(5,b);
		X10DepTypeClassOne toSuperWider = (X10DepTypeClassOne(:p==5)) toObj;

		// invalid constraint cast
		X10DepTypeSubClassOne(:p==4) toImpl3 = (X10DepTypeSubClassOne(:p==4)) toSuperWider;
		}catch (ClassCastException e) {
			return true;			
		}
		return false;
	}

	public static void main(String [] args ) {
		new DynamicCastDownCastHeritage().execute();
	}
}

