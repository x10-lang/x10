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
public class Generics1 extends x10Test {
        class Get[T] { x: T; def this(y: T) = { x = y } def get(): T = x; }

	public def run(): boolean = {
                val a: int = new Get[int](0).get();
                val b: String = new Get[String]("").get();
                val c: Generics1 = new Get[Generics1](this).get();
		return a == 0 && b == "" && c == this;
	}

	public static def main(var args: Rail[String]): void = {
		new Generics1().execute();
	}
}

