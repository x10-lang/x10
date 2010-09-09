/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Inference for local variables.
 *
 * @author nystrom 8/2008
 */
public class Inference1 extends x10Test {
	public def run(): boolean = {
                val x = 1;
                val y: int = x;
		return x == y;
	}

	public static def main(var args: Rail[String]): void = {
		new Inference1().execute();
	}
}

