/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Automatic boxing and un-boxing of a final value class
 * when up-casting, and down-casting.
 */
public class Boxing1 extends x10Test {
	public def run(): boolean = {
		var o: x10.lang.Object = X.five();
		x10.io.Console.OUT.println("int");
		if (!(o instanceof Box[int])) return false;
		x10.io.Console.OUT.println("double");
		if (o instanceof Box[double]) return false;
		var i: int = (o as int) + 1;
		x10.io.Console.OUT.println("6");
		if (i != 6) return false;
		var d: _dummy = new _complex(1,2);
		o = d;
		x10.io.Console.OUT.println("d _complex");
		if (!(d instanceof Box[_complex])) return false;
		x10.io.Console.OUT.println("o _dummy");
		if (!(o instanceof _dummy)) return false;
		x10.io.Console.OUT.println("o _complex");
		if (!(o instanceof Box[_complex])) return false;
		var d2: _dummy = new _dummy();
		x10.io.Console.OUT.println("d2 _complex");
		if (d2 instanceof _complex) return false;
		var c: _complex = (d as _complex).add(new _complex(1,1));
		x10.io.Console.OUT.println("c _complex");
		if (c != (new _complex(2,3))) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Boxing1().execute();
	}

	static class X {
		public static def five(): int = 5;
	}
}
