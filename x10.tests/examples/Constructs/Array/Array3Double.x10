/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Ensures double arrays are implemented.
 */
public class Array3Double extends x10Test {

	public def run(): boolean = {
	    val r:Region(2) = Region.make([1..10, 1..10]);
	    var ia: Array[Double](2) = Array.makeFromRegion[Double](r, (x:Point)=>0.0D);
	    ia(1, 1) = 42.0D;
	   System.out.println("ia(1,1)=" + ia(1,1));
	    return 42.0D == ia(1,1);
	}

	public static def main(Rail[String]) = {
		new Array3Double().execute();
	}
}
