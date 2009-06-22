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
public class Generics1 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) = { x = y; }
                def get(): T = x; }

	public def run(): boolean = {
                val c: Generics1 = new Get[Generics1](this).get();
                System.out.println("c = " + c);
                System.out.println("this = " + this);
		return c == this;
	}

	public static def main(var args: Rail[String]): void = {
		new Generics1().execute();
	}
}

