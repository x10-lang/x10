/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Synthetic benchmark to time array accesses.
 */
public class IntArrayIndexing extends x10Test {

	static verbose: boolean = false;

	var _intArray1D: Array[int](1);
	var _intArray2D: Array[int](2);
	var _intArray3D: Array[int](3);
	var _intArray4D: Array[int](4);

	public def this(): IntArrayIndexing {
		val kArraySize: long = 30;
		var range1D: Region(1);
                var range2D: Region(2);
                var range3D: Region(3);
                var range4D: Region(4);

		// Note: cannot do anything fancy with starting index--assume 0 based
		range1D = Region.make(0, kArraySize);
		range2D = Region.make(0..kArraySize, 0..kArraySize);
		range4D = Region.make(0..2, 0..4, 2..10, 1..10);
		range3D = Region.make(0..11, 0..6, 0..7); 

		var start: long = System.currentTimeMillis();
                _intArray1D = new Array[int](range1D);
                _intArray2D = new Array[int](range2D);
                _intArray3D = new Array[int](range3D);
                _intArray4D = new Array[int](range4D);
		var stop: long = System.currentTimeMillis();
		x10.io.Console.OUT.println("int arrays allocated in "+(((stop-start) as double)/1000)+ "seconds");
	}

	def verify3D(var array: Array[int](3)): boolean {

		var h1: long = array.region.max(0);
		var h2: long = array.region.max(1);
		var h3: long = array.region.max(2);

		var l1: long = array.region.min(0);
		var l2: long = array.region.min(1);
		var l3: long = array.region.min(2);

		var count: int = 0n;
		for (var i: long = l1; i <= h1; ++i)
			for (var j: long = l2; j <= h2; ++j)
				for (var k: long = l3; k <= h3; ++k) {
					//x10.io.Console.OUT.println("value is:"+array[i,j,k]);
					array(i, j, k) = array(i, j, k);
					if (verbose) x10.io.Console.OUT.println("a["+i+","+j+","+k+"] = "+count);
					if ( array(i, j, k) != count) {
						x10.io.Console.OUT.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[int](4)): boolean {
		var h1: long = array.region.max(0);
		var h2: long = array.region.max(1);
		var h3: long = array.region.max(2);
		var h4: long = array.region.max(3);
		var l1: long = array.region.min(0);
		var l2: long = array.region.min(1);
		var l3: long = array.region.min(2);
		var l4: long = array.region.min(3);
		var count: int = 0n;
		for (var i: long = l1; i <= h1; ++i)
			for (var j: long = l2; j <= h2; ++j)
				for (var k: long = l3; k <= h3; ++k)
					for (var l: long = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) x10.io.Console.OUT.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if ( array(i, j, k, l) != count) {
							x10.io.Console.OUT.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	def initialize(val array: Array[int]): void {
		var count: int = 0n;
		for (val p: Point(array.rank) in array.region) {
			array(p) = count++;
			if (verbose) x10.io.Console.OUT.println("init:"+p+" = "+count);
		}
	}

	def runIntTests(var repeatCount: int): boolean {
		x10.io.Console.OUT.println("Testing Ints...");
		var start: long = System.currentTimeMillis();
		initialize(_intArray3D);
		while (repeatCount-- > 0)
			if (!verify3D(_intArray3D)) return false;

		x10.io.Console.OUT.println("Testing of 3D int arrays took "+(((System.currentTimeMillis()-start) as double)/1000));
		initialize(_intArray4D);
		if (false) {
			while (repeatCount-- > 0)
				if (!verify4D(_intArray4D)) return false;
		}

		var stop: long = System.currentTimeMillis();
		x10.io.Console.OUT.println("Testing of int arrays took "+(((stop-start) as double)/1000));
		return true;
	}

	public def run(): boolean {
		var repeatCount: int = 4000n;

		if (!runIntTests(repeatCount)) return false;

		return true;
	}

	public static def main(var args: Rail[String]): void {
		new IntArrayIndexing().execute();
	}
}
