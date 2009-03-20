/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Inference of method type parameters and method return type
 * with generic types.
 *
 * @author nystrom 8/2008
 */
public class Inference6 extends x10Test {
        def m[T](x: ValRail[T]) = x(0);

	public def run(): boolean = {
                val x = m([1]);
                val y: int = x;
		return y == 1;
	}

	public static def main(var args: Rail[String]): void = {
		new Inference6().execute();
	}
}

