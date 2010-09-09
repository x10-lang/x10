/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks both litteral is directly converted to its Boxed representation
 *          and is an instanceof its nullable primitive type.
 * @author vcave
 **/
public class PrimitiveToNullablePrimitive1 extends x10Test {
	 
	public boolean run() {
		// transformed to new BoxedInteger(3) instanceof /*nullable*/BoxedInteger
		return (3 instanceof nullable<int>);
	}
	
	public static void main(String[] args) {
		new PrimitiveToNullablePrimitive1().execute();
	}
}
 