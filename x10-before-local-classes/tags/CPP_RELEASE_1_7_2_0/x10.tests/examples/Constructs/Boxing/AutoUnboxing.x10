/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests that a variable of type Box[int] is automatically unboxed to int.
 */
public class AutoUnboxing extends x10Test {

        public def run(): boolean = {
                val x:Box[Int] = 5;
                return x==5;
        }

	public static def main(var args: Rail[String]): void = {
		new AutoUnboxing().execute();
	}
}
