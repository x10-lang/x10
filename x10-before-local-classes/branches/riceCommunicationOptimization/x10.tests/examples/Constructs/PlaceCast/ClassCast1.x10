/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks whether a cast of null to a non-nullable type
 * throws a ClassCastException.
 * @author vj 6/4/2005
 */
public value class ClassCast1 extends x10Test {

	public def run(): boolean = {
		try {
			val A: Rail[Box[ClassCast1]] = [ null, new ClassCast1() ];
			var v: Box[ClassCast1] = (A(0) == null) ? null : A(1);
			return new ClassCast1() == v as ClassCast1; // should throw a ClassCastException
		} catch (e: ClassCastException) { // Per Sec 11.4.1, v 0.409 of the manual
			return true;
		}
	}

	public static def main(var args: Rail[String]): void = {
		new ClassCast1().execute();
	}
}
