/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;
import x10.array.Dist;
import x10.array.Array;
import x10.array.Region;

/**
 * Synthetic benchmark to time arary accesses.
 */
public class Initialization extends x10Test {

	var _tests: Rail[String] = [ "testDouble" ];
	const kArraySize: int = 500;
	var x_doubleArray1D: Array[double];
	var x_doubleArray2D: Array[double];
	var x_javaArray: Array[double];
	var x_intArray1D: Array[int];

	public def run(): boolean = {
		var start: long;
                var stop: long;
		val OneDSize: int = kArraySize * kArraySize;

		start = System.currentTimeMillis();
		x10.io.Console.OUT.println("creating java array size "+OneDSize);
		x_javaArray = Rail.make[double](OneDSize);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created array in "+(((stop-start) as double)/1000)+" seconds");

		start = System.currentTimeMillis();
		x10.io.Console.OUT.println("creating array size "+OneDSize);
		var r: Region = [0..OneDSize];
		val D: Dist = Dist.makeBlock(r);
		x_doubleArray1D = Array.make[double](D);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created array in "+(((stop-start) as double)/1000)+" seconds");

		x10.io.Console.OUT.println("creating array ["+kArraySize+","+kArraySize+"] ("+(kArraySize*kArraySize)+")");
		var r2: Region = [0..kArraySize, 0..kArraySize];
		val D2: Dist = Dist.makeBlock(r2);
		x10.io.Console.OUT.println("Start allocation...");
		start = System.currentTimeMillis();
		x_doubleArray2D = Array.make[double](D2);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created array in "+(((stop-start) as double)/1000)+" seconds");
		x10.io.Console.OUT.println("finished allocating");

		start = System.currentTimeMillis();
		x_intArray1D = Array.make[int](D);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created int array in "+(((stop-start) as double)/1000)+" seconds");
		x10.io.Console.OUT.println("finished allocating");
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Initialization().execute();
	}
}
