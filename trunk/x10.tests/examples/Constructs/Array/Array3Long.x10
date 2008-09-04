/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Ensures long arrays are implemented.
 */
public class Array3Long extends x10Test {

	public def run(): boolean = {
	val r:Region{rank==2} = Region.make([1..10, 1..10]);
	    var ia: Array[Long]{rank==2} = Array.makeFromRegion[Long](r, (x:Point)=>0L);
		ia(1, 1) = 42L;
		return 42L == ia(1, 1);
	}

	public static def main(var args: Rail[String]): void = {
		new Array3Long().execute();
	}
}
