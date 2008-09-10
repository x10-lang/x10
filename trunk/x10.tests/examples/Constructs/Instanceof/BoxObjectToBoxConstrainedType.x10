/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Purpose: 
 * @author vcave
 Converted to Box by vj.
 **/
public class BoxObjectToBoxConstrainedType extends x10Test {
	 
	public def run(): boolean = {
		var n: Box[ValueClass{p==1}] = new ValueClass(1);
		return n instanceof Box[ValueClass{p==1}];
	}
	
	public static def main(var args: Rail[String]): void = {
		new BoxObjectToBoxConstrainedType().execute();
	}
}
