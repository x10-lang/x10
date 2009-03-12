/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * @author Christian Grothoff
 */
public class ValueEquality extends x10Test {
	public def run(): boolean = {
		var v1: V = new V(1);
		var v2: V = new V(1);
		return v1 == v2;
	}

	public static def main(args: Rail[String]): void = {
		new ValueEquality().execute();
	}

	static value V {
		val v: int;
		def this(i: int): V = {
			this.v = i;
		}
	}
}
