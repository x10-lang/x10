/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a boxed primitive is an instanceof this same primitive type .
 * @author vcave
 **/
public class ObjectToPrimitive extends x10Test {
	 
	public boolean run() {
		x10.lang.Object primitive = 3;
		return (primitive instanceof int);
	}
	
	public static void main(String[] args) {
		new ObjectToPrimitive().execute();
	}
}
 