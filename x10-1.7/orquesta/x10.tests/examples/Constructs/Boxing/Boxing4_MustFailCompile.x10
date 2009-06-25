/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests interaction of parentheses and boxing
 */
public class Boxing4_MustFailCompile extends x10Test {

        public def run(): boolean = {
                val v: Box[int] = 0;
                val a: int = v as int; // error
                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new Boxing4_MustFailCompile().execute();
	}
}

