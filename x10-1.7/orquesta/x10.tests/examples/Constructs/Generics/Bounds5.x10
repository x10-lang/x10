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
public class Bounds5 extends x10Test {
        class A { }
        class B extends A { }

        class C[T]{T<:A}
                var x: T;
                def this(y: T) = { x = y; }
        }

	public def run(): boolean = {
                val b = new B();
                return new C[B](b).x == b;
	}

	public static def main(var args: Rail[String]): void = {
		new Bounds5().execute();
	}
}

