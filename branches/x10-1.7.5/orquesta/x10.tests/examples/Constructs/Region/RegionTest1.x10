/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Minimal test for regions.
 */
public class RegionTest1 extends x10Test {

	public def run(): boolean = {
		var r: region = region.make(0, 100); // (low, high)
		var reg: region = region.make(r, r);

		var sum: int = 0;
		for (val p: point in reg) sum += p.get(0) - p.get(1);

		return sum == 0;
	}

	public static def main(var args: Rail[String]): void = {
		new RegionTest1().execute();
	}
}
