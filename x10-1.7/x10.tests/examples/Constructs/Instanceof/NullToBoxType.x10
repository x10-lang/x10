/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks a null value is not an instance of a Boxtype
 * Note: The compiler statically replaces instanceof expression by true
 * @author vcave
   @author vj
 **/
public class NullToBoxType extends x10Test {
	 
	public def run(): boolean = {
		return !(null instanceof Box[ValueClass]);
	}
	
	public static def main(var args: Rail[String]): void = {
		new NullToBoxType().execute();
	}
}
