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
public class ClassCast1 extends x10Test {
        static struct CC1 {
	    def this() {} 
            public def typeName() = "CC1";
        }

	public def run(): boolean = {
		try {
			val A: Rail[Box[CC1]]! = [ null, new Box[CC1](CC1()) ];
			var v: Box[CC1]! = (A(0) == null) ? null : A(1);
			return CC1() == (v as CC1); // should throw a ClassCastException
		} catch (e: ClassCastException) { // Per Sec 11.4.1, v 0.409 of the manual
			return true;
		}
	}

	public static def main(var args: Rail[String]): void = {
		new ClassCast1().execute();
	}
}
