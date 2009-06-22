/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks both literal is directly converted to its Boxed representation
 *          and is an instanceof its Boxed primitive type.
 * @author vcave
 **/
public class PrimitiveToBoxPrimitive1_MustFailCompile extends x10Test {
	 
	public def run()=3 instanceof Box[int];
	
	public static def main(var args: Rail[String]): void = {
		new PrimitiveToBoxPrimitive1_MustFailCompile().execute();
	}
}
