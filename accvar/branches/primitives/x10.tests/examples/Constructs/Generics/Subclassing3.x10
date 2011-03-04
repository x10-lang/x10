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
public class Subclassing3 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) = { x = y; }
                def get(): T = x; }
        class GetA extends Get[A] {
                def this(y: A) = { super(y); } }

        class A { }
        class B extends A { }

        public def run(): boolean = {
                new Get[A](new A());
                new Get[B](new B());
                new GetA(new A());
                new GetA(new B());
                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new Subclassing3().execute();
	}
}

