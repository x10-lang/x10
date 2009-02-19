/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
//import harness.x10Test;

/**
 * Test closures.
 *
 * @author nystrom 8/2008
 */
public class Closures1nh { //extends x10Test {
	public def run(): boolean = {
				val k = (i:int) => (3 + i);
                val j = k(4);
                return false;
	}

	public static def main(var args: Rail[String]): void = {
		new Closures1nh().run();
	}
}

