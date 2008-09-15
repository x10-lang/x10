/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing the maxAbs function on arrays.
 */
public class IntArrayMaxAbs extends x10Test {

	public def run(): boolean = {
		val D  = Dist.makeConstant([1..10, 1..10] to Region, here);
		val ia  = Array.make[int](D, (point)=>0);

		finish ateach (val p(i,j): point(2) in D) { ia(p) = -i; }

		return ia.reduce((a:Int, b:Int)=> 
		   Math.abs(a) <= Math.abs(b) ? b : a
		) == 10;
	}

	public static def main(var args: Rail[String]): void = {
		new IntArrayMaxAbs().execute();
	}
}
