/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks cast from nullable to non-nullable that implies 
 *          boxing/unboxing operation.
 * Issue: The nullable primitive is null which makes the cast to a non-nullable primitive fail.
 * @author vcave
 **/
 public class CastNullablePrimitiveToPrimitive extends x10Test {

	public boolean run() {
		try {
			nullable<int> k = null; // type becomes nullable<x10.compiler.BoxedInt>
			int p = (int) k; // cast is transformed to ((BoxedInt) k).intValue() 
			// --> fails because 'k' is null which throws a ClassCastException
		} catch (ClassCastException e) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		new CastNullablePrimitiveToPrimitive().execute();
	}
}