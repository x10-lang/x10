/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Region algebra.
 *
 * @author nystrom 8/2008
 */
public class Variance3 extends x10Test {
        class Set[-T] { var x: T;
                        def this(y: T) = { x = y; }
                        def set(y: T): void = { x = y; }  }

        class A { }
        class B extends A { }

        public def run(): boolean = {
                val a = new A();
                val b = new B();

                val sa = new Set[A](a);
                val sb = new Set[B](b);

                sa.set(b);
                sb.set(b);

                val sx: Set[B]! = sa;
                sx.set(b);

                val sy: Set[B]! = sb;
                sy.set(b);

                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new Variance3().execute();
	}
}

