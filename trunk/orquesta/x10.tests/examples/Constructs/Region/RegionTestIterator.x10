/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Iterator;
import harness.x10Test;;

/**
 * Testing the standard region iterator.
 */
public class RegionTestIterator extends x10Test {

	public def run(): boolean = {
		var r: region = region.make(0, 100); // (low, high)
		var r2: Rail[region] = [ r, r ];
		var reg: region = region.make(r2);

		var sum: int = 0;
		for (var it: Iterator[Point] = reg.iterator(); it.hasNext(); ) {
			var p: point = (point) it.next();
			sum += p(0)-p(1);
		}
		// for (point [i,j]: reg) sum += i - j;
		return sum == 0;
	}

	public static def main(var args: Rail[String]): void = {
		new RegionTestIterator().execute();
	}
}
