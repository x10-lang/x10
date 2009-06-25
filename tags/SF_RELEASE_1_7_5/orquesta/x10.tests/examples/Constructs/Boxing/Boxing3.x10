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
public class Boxing3 extends x10Test {

        public def run(): boolean = {
                val x: int = 0;
                val y: Object = 0;
                val z: Object = x;
                val v: Box[int] = 0;
                val w: Box[int] = x;
                val u: int = v to int;
                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new Boxing3().execute();
	}
}
