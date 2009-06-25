/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Static mutable fields are not allowed in x10.
 *
 * (leads to complexities in defining the place of static
 * fields of a class).
 *
 * @author kemal 5/2005
 */
public class NoStaticMutable1_MustFailCompile extends x10Test {
	//<== compiler error must occur on next line
	static var x1: int = 0;

	const x2: int = 0;
	public const x3: int = 0;

	//<== compiler error must occur on next line
	static var f1: foo = new foo(1);

	const f2: foo = new foo(1);
	public const f3: foo = new foo(1);

	public def run(): boolean = {
		x1++;
		f1 = new foo(2);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new NoStaticMutable1_MustFailCompile().execute();
	}

	static class foo {
		var val: int;
		def this(var x: int): foo = { val = x; }
	}
}
