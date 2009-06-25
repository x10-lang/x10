/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test type defs in method bodies.
 *
 * @author nystrom 8/2008
 */
public class Typedef1 extends x10Test {
	public def run(): boolean = {
                type foo = int;
                type bar = Typedef1;
                val x: foo = 3;
                val y: bar = this;
		return x == 3 && y == this;
	}

	public static def main(var args: Rail[String]): void = {
		new Typedef1().execute();
	}
}

