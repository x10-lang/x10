/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
import x10.lang.Object;
import x10.lang.Integer;

/**
 * This produces an infinite loop for the type checker, on 06/25/06
 */
public class FieldAccessTest_MustFailCompile extends x10Test {
	public nullable<Object> n;

	/**
	 * The method is deliberately type-incorrect.
	 * It should return nullable Object.
	 * The problem is that this incorrect program causes the compiler to loop.
	 */
	public FieldAccessTest_MustFailCompile n() {
		return n;
	}

	public boolean run() {
		return true;
	}

	public static void main(String[] args) {
		new FieldAccessTest_MustFailCompile().execute();
	}
}

