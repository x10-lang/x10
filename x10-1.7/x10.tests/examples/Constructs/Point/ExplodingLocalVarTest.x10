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
public class ExplodingLocalVarTest extends x10Test {

	public def run(): boolean = {
		val p(x,y):Point  = [2, 2];
		return x+y==4;
		}

	public static def main(var args: Rail[String]): void = {
		new ExplodingLocalVarTest().execute();
	}
}
