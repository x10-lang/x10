/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing returns in an async body. 
 * New semantics for X10 1.7 Cannot return from the body of an async.
 *
 * @author vj
 * updated 03/15/09
 */
public class AsyncReturn_MustFailCompile extends x10Test {

	public def run(): boolean = {
		finish async {
				return;
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncReturn_MustFailCompile().execute();
	}
}
