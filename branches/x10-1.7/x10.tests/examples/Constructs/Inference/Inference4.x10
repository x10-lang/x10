/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Inference of method type parameters.
 *
 * @author nystrom 8/2008
 */
public class Inference4 extends x10Test {
        def m[T](x: T): T = x;

	public def run(): boolean = {
                val x = m(1);
                val y: int = x;
		return x == y;
	}

	public static def main(var args: Rail[String]): void = {
		new Inference4().execute();
	}
}

