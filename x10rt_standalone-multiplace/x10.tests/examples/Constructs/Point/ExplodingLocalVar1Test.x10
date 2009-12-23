/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing exploding syntax for local variables.
 *
 * @author vj 09/02/08
 */
public class ExplodingLocalVar1Test extends x10Test {

	public def run(): boolean = {
	    // the type Point is not supplied. 
	    // This should really not work, according to Sec 4.13.1 (type inference)
	    // and Section 10 (destructuring syntax). But it does.
		val p(x,y)  = [2, 2];
		return x+y==4 && p(0)+p(1)==4;
		}

	public static def main(var args: Rail[String]): void = {
		new ExplodingLocalVar1Test().execute();
	}
}
