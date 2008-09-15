/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing that disjoint union of dists
 * actually checks for disjointness.
 *
 * @author kemal 4/2005
 */
public class DistAlgebra3_MustFailRun extends x10Test {

	const N = 24;

	public def run(): boolean = {
		val D  = dist.makeCyclic(0..N-1);
		val D2  = D | 0..N/2-1;
		val D3  = D | (N/2)..N-1; 
		val D4  = D2 || D3; // disjoint
		chk(D4.equals(D));
		val D5 = D || D2; // not disjoint
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new DistAlgebra3_MustFailRun().execute();
	}
}
