/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that a value stored in a variable of type Box[C], for C a reference class, 
 * is in fact an instanceof C.
 */
public class Boxing6 extends x10Test {

        static class C { }

        public def run(): boolean = {
                val x = new C();
                val y = x to Box[C];
                return y instanceof C;
        }

	public static def main(var args: Rail[String]): void = {
		new Boxing6().execute();
	}
}
