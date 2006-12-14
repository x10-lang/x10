/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a constrained cast leading to primitive unboxing works 
 *          actually checks the unboxed primitive.
 * @author vcave
 **/
 public class CastBoxedToNullablePrimitiveConstrained extends x10Test {

	public boolean run() {
		// transformed to (nullable<int(:self==3)>) ((BoxedInteger) obj).intValue();
		// which means the actual value of the boxed integer will be checked.
		nullable<int(:self==3)> i = (nullable<int(:self==3)>) mth();
		return true;
	}
	
	public x10.lang.Object mth() {
		// boxed to BoxedInteger(3);
		return 3;
	}
	public static void main(String[] args) {
		new CastBoxedToNullablePrimitiveConstrained().execute();
	}
}