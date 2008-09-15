/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test a generic class instantiated on a class with a Java rep.
 *
 * @author nystrom 8/2008
 */
public class Generics2 extends x10Test {
        class Get[T] { val x: T; def this(y: T) = { x = y; } def get(): T = x; }

	public def run(): boolean = {
                val b: String = new Get[String]("").get();
		return b == "";
	}

	public static def main(var args: Rail[String]): void = {
		new Generics2().execute();
	}
}

