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
public class NullToBoxPrimitive extends x10Test {
	 
	public def run() = !(null instanceof Box[Int]);
	
	public static def main(var args: Rail[String]): void = {
		new NullToBoxPrimitive().execute();
	}
}
