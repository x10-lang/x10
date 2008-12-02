
/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 * @author bdlucas 8/2008
 */
import harness.x10Test;

/**
 * Test closures.
 *
 * @author nystrom 8/2008
 */
public class Closures3 extends x10Test {
	public def run(): boolean = {
                val j = ((i: int, j: int) => i+j)(3,4);
                return j == 7;
	}

	public static def main(var args: Rail[String]): void = {
		new Closures3().execute();
	}
}

