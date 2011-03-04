/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a primitive literal is an instanceof x10.lang.Object
 * NoteL: Obviously the Boxed representation extends x10.lang.Object
 * @author vcave
 **/
public class PrimitiveToObject extends x10Test {
	 
	public def run() = 3 instanceof Object;
		
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToObject().execute();
	}
}
