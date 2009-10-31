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
public class Variance1 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) = { x = y; }
                def get(): T = x; }

        class A { }
        class B extends A { }

        public def run(): boolean = {
                val ga = new Get[A](new A());
                val gb = new Get[B](new B());
                val a = ga.get();
                val b = gb.get();

                val gx : Get[A]! = ga;
                val x = gx.get();
                val gy : Get[B]! = gb;
                val y = gy.get();

                return gx == ga && gy == gb && x == a && y == b;
        }

	public static def main(var args: Rail[String]): void = {
		new Variance1().execute();
	}
}

