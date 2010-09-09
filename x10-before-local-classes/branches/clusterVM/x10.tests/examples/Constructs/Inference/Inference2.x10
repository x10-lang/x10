/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Inference of method type parameters and method return type.
 *
 * @author nystrom 8/2008
 */
public class Inference2 extends x10Test {
        def m[T](x: T) = x;

	public def run(): boolean = {
                val x = m(1);
                val y: int = x;
		return x == y;
	}

	public static def main(var args: Rail[String]): void = {
		new Inference2().execute();
	}
}

