/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.util.Box;
import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 Converted as Box by vj.
 **/
public class BoxObjectToBoxConstrainedType extends x10Test {
	 
	public def run(): boolean = {
		val n = new ValueClass(1);
		return n instanceof Box[ValueClass{p==1}];
	}
	
	public static def main(var args: Rail[String]): void = {
		new BoxObjectToBoxConstrainedType().execute();
	}
}
