/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test type parameter bounds.
 *
 * @author nystrom 8/2008
 */
public class Assign1 extends x10Test {
	public def run(): boolean = {
                val r = Rail.makeVar[int](1, (int) => 0);
                r(0) = 0;
                r(r(0)) += 5;
                return r(0) == 5;
	}

	public static def main(var args: Rail[String]): void = {
		new Assign1().execute();
	}
}

