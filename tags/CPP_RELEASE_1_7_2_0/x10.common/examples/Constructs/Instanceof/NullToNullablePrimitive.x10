/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Check null is an instanceof a nullable primitive.
 * Note: The compiler directly replace instanceof expression by true.
 * @author vcave
 **/
public class NullToNullablePrimitive extends x10Test {
	 
	public boolean run() {
		return !(null instanceof nullable<int>);
	}
	
	public static void main(String[] args) {
		new NullToNullablePrimitive().execute();
	}
}
 