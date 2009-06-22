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
		System.out.println("int");
		if (!(o instanceof int)) return false;
		System.out.println("double");
		if (o instanceof double) return false;
		var i: int = (o to int) + 1;
		System.out.println("6");
		if (i != 6) return false;
		var d: _dummy = new _complex(1,2);
		o = d;
		System.out.println("d _complex");
		if (!(d instanceof _complex)) return false;
		System.out.println("o _dummy");
		if (!(o instanceof _dummy)) return false;
		System.out.println("o _complex");
		if (!(o instanceof _complex)) return false;
		var d2: _dummy = new _dummy();
		System.out.println("d2 _complex");
		if (d2 instanceof _complex) return false;
		var c: _complex = ((_complex)d).add(new _complex(1,1));
		System.out.println("c _complex");
		if (c != (new _complex(2,3))) return false;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Boxing1().execute();
	}

	static class X {
		public static def five(): int = { return 5; }
	}
}
