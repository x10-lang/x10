/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class ObjectToNullablePrimitiveConstrained2 extends x10Test {
	 
	public boolean run() {
		x10.lang.Object primitive = 3;
		return !(primitive instanceof nullable<int(:self==4)>);
	}
	
	public static void main(String[] args) {
		new ObjectToNullablePrimitiveConstrained2().execute();
	}
}
 