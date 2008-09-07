/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests autoboxing when assigned a value to a variable declared at an interface type.
 */
public class Boxing5 extends x10Test {

        static interface I { }
        static value V implements I { }

        public def run(): boolean = {
                val v: V = new V();
                val x: I = v; // should auto box
                return x instanceof Box[V];
        }

	public static def main(var args: Rail[String]): void = {
		new Boxing5().execute();
	}
}
