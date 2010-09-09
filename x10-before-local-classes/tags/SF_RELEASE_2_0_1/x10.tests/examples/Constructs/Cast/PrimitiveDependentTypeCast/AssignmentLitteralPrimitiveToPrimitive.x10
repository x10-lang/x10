/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks assignment of primitive to constrained primitives.
 * @author vcave
 **/
public class AssignmentLitteralPrimitiveToPrimitive extends x10Test {

	 public def run(): boolean = {
		var bb: byte{self==1} = 1;
		var ss: short{self==10} = 10;
		var ii: int{self==20} = 20;
		var iii: int{self==-2} = -2;
		var ll: long{self==30} = 30;
		var ff: float{self==0.001F} = 0.001F;
		var i: double{self == 0.001} = 0.001;
		var cc: char{self=='c'} = 'c';
		
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentLitteralPrimitiveToPrimitive().execute();
	}
}
