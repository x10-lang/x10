/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Ensures short arrays are implemented.
 */
public class Array3Short extends x10Test {

	public def run(): boolean = {
	val r:Region{rank==2} = Region.make([1..10, 1..10]);
	    var ia: Array[Short]{rank==2} = Array.makeFromRegion[Short](r, (x:Point)=>(0 to Short));
		ia(1, 1) = 42 to Short;
		return (42 == ia(1, 1));
	}

	public static def main(var args: Rail[String]): void = {
		new Array3Short().execute();
	}
}
