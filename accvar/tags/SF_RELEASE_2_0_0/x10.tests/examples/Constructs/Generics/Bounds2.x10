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
public class Bounds2 extends x10Test {
        class C[T]{T<:String} {
                var x: T;
                def this(y: T) = { x = y; }
        }

	public def run(): boolean = {
                return new C[String]("").x.equals("");
	}

	public static def main(var args: Rail[String]): void = {
		new Bounds2().execute();
	}
}

