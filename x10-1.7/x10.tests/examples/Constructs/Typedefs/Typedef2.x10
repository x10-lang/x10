/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test a generics class with an invariant parameter.
 *
 * @author nystrom 8/2008
 */
public class Typedef2 extends x10Test {
	public def run(): boolean = {
                type foo[T] = T;
                type bar(x: int) = int{self==x};
                val x: foo[int] = 3;
                val y: bar(5) = 5;
		return x == 3 && y == 5;
	}

	public static def main(var args: Rail[String]): void = {
		new Typedef2().execute();
	}
}

