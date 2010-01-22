/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks if a struct literal is an instanceof x10.lang.Any
 * @author vcave
 **/
public class PrimitiveToObject extends x10Test {
	 
	public def run() = 3 instanceof Any;
		
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToObject().execute();
	}
}
