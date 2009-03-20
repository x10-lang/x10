/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks cast leading to primitive unboxing works.
 * @author vcave
 **/
 public class CastBoxedToNullablePrimitive extends x10Test {

	public boolean run() {
		// transformed to (nullable<int>) ((BoxedInteger) obj).intValue();
		nullable<int> i = (nullable<int>) mth();
		return true;
	}
	
	public x10.lang.Object mth() {
		// boxed to BoxedInteger(3);
		return 3;
	}
	public static void main(String[] args) {
		new CastBoxedToNullablePrimitive().execute();
	}
}