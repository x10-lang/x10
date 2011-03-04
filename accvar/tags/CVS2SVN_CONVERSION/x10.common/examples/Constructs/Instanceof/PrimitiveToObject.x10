/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a primitive litteral is an instanceof x10.lang.Object
 * NoteL: Obviously the Boxed representation extends x10.lang.Object
 * @author vcave
 **/
public class PrimitiveToObject extends x10Test {
	 
	public boolean run() {
		return (3 instanceof x10.lang.Object);
	}
	
	public static void main(String[] args) {
		new PrimitiveToObject().execute();
	}
}
 