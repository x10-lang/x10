/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Synthetic benchmark to time arary accesses.
 */
public class IntArrayIndexing extends x10Test {

	var _tests: Rail[String] = { "testDouble" };

	const verbose: boolean = false;

	var _intArray1D: Array[int];
	var _intArray2D: Array[int];
	var _intArray3D: Array[int];
	var _intArray4D: Array[int];

	public def this(): IntArrayIndexing = {
		val kArraySize: int = 30;
		var range1D: regionvar range2D: regionvar range3D: regionvar range4D: region;

		// Note: cannot do anything fancy with starting index--assume 0 based
		range1D = [0..kArraySize];
		range2D = [0..kArraySize, 0..kArraySize];
		range4D = [0..2, 0..4, 2..10, 1..10];
		//range3D = [0:13,0:37,0:19]; // make it zero based
		range3D = [0..11, 0..6, 0..7]; // reducing size further

		var start: long = System.currentTimeMillis();
		_intArray1D = new Array[int](dist.factory.constant(range1D,here));
		_intArray2D = new Array[int](dist.factory.constant(range2D,here));
		_intArray3D = new Array[int](dist.factory.constant(range3D,here));
		_intArray4D = new Array[int](dist.factory.constant(range4D,here));
		var stop: long = System.currentTimeMillis();
		System.out.println("int arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");
	}

	def verify3D(var array: Array[int]): boolean = {

		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();

		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();

		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					//System.out.println("value is:"+array[i,j,k]);
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if ( array(i, j, k) != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[int]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if ( array(i, j, k, l) != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	def initialize(var array: Array[int]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}

	def runIntTests(var repeatCount: int): boolean = {
		System.out.println("Testing Ints...");
		var start: long = System.currentTimeMillis();
		initialize(_intArray3D);
		while (repeatCount-- > 0)
			if (!verify3D(_intArray3D)) return false;

		System.out.println("Testing of 3D int arrays took "+(((System.currentTimeMillis()-start) to double)/1000));
		initialize(_intArray4D);
		if (false) {
			while (repeatCount-- > 0)
				if (!verify4D(_intArray4D)) return false;
		}

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of int arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	public def run(): boolean = {
		var repeatCount: int = 4000;

		if (!runIntTests(repeatCount)) return false;

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntArrayIndexing().execute();
	}

	static class Generic {
		public var value: int;
	}
}
