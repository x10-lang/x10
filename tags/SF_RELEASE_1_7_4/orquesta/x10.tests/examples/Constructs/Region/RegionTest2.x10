/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Minimal test for the empty region.
 */
public class RegionTest2 extends x10Test {

	public def run(): boolean = {
		var reg: region = region.make(0, -1); // [0..-1]

		var sum: int = 0;
		for (val p: point in reg) sum++;
		return sum == 0;
	}

	public static def main(var args: Rail[String]): void = {
		new RegionTest2().execute();
	}
}
