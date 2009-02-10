/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that a variable of type Box[int] is automatically unboxed as int.
 */
public class AutoUnboxing_MustFailCompile extends x10Test {

        public def run(): boolean = {
                val x:Box[Int] = 5;
                return x==5; // Compiler should not auto-unbox
        }

	public static def main(var args: Rail[String]): void = {
		new AutoUnboxing().execute();
	}
}
