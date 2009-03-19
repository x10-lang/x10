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
public class Subclassing4 extends x10Test {
        class Get[T] {
                val x: T;
                def this(y: T) = { x = y; }
                def get(): T = x; }
        class Getint extends Get[int] {
                def this(y: int) = { super(y); } }

	public def run(): boolean = {
                new Get[int](0);
                new Get[int](1);
                new Getint(2);
                new Getint(3);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Subclassing4().execute();
	}
}
