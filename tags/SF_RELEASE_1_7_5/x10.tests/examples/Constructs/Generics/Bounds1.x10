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
public class Bounds1 extends x10Test {
        class C[T]{T==String} {
                val x: T = "";
        }

	public def run(): boolean = {
                return new C[String]().x == "";
	}

	public static def main(var args: Rail[String]): void = {
		new Bounds1().execute();
	}
}

