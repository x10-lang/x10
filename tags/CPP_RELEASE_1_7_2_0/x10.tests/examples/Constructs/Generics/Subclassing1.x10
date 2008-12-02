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
public class Subclassing1 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) = { x = y; }
                def get(): T = x; }
        class Get2[T] extends Get[T] {
                def this(y: T) = { super(y); } }

        class A { }
        class B extends A { }

        public def run(): boolean = {
                new Get[A](new A());
                new Get[B](new B());
                new Get2[A](new A());
                new Get2[B](new B());
                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new Subclassing1().execute();
	}
}

