/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
import x10.lang.Math;
/**
 * 
 *
 * Submitted by Doug Lovell.
 * @author igor 1/2006
 * @author vj 09/2008 -- Apparently the compiler cant deal with a(i)(j) = e.
 */
public class MultiDimensionalJavaArray extends x10Test {

	const MIN = 0..99;
	const MAJ = 0..9;
	const PI = Math.PI;

	public def run(): boolean = {
		val a = Array.make[Array[Double]](MIN, 
		  (point) => Array.make[Double](MAJ, (point)=>0.0D));
//		  ^ MultiDimensionalJavaArray.x10:15: Cannot assign double[] to double[][].
        for (val (i,j):Point(2) in ([MIN, MAJ] to Region)) {
				a(i)(j) = (i * j /PI);
		}
		val d = a(MAJ/2);
		for (val (j): Point in MIN) 
			chk(d(j) == (MAJ/2 * j / PI));
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new MultiDimensionalJavaArray().execute();
	}
}
