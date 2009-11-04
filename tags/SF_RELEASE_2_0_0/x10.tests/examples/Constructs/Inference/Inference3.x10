/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Inference for return types.
 *
 * @author nystrom 8/2008
 */
public class Inference3 extends x10Test {
        def m() = 3;

	public def run(): boolean = {
                val x = m();
                val y: int = x;
		return x == y;
	}

	public static def main(var args: Rail[String]): void = {
		new Inference3().execute();
	}
}

