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
public class Generics4 extends x10Test {
        class Gen[T] {
                var y: T;
                
                public def this(x: T) = { y = x; }
                
                public def set(x: T): void = {
                  y = x;
                }
                
                public def get(): T = y;
        }

        public def run(): boolean = {
                var result: boolean = true;

                s: Gen[String] = new Gen[String]("hi");
                x: String = s.get();

                s.set("hello");
                y: String = s.get();

                result &= x.equals("hi");
                result &= y.equals("hello");

                return result;
        }

	public static def main(var args: Rail[String]): void = {
		new Generics4().execute();
	}
}

