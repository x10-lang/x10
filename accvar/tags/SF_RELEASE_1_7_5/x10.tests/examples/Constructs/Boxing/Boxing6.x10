/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that a value stored in a variable of type Box[C], is a Box[C] not a C.
 */
public class Boxing6 extends x10Test {

        static class C { }

        public def run(): boolean = {
                val x = new C();
                val y = x as Box[C];
                return y instanceof Box[C];
        }

	public static def main(var args: Rail[String]): void = {
		new Boxing6().execute();
	}
}
