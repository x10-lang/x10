/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Synthetic benchmark to time array initialization.
 */
public class Initialization extends x10Test {

	static kArraySize: int = 500n;
	var x_doubleArray1D: DistArray[double];
	var x_doubleArray2D: DistArray[double];
	var x_javaArray: Array[double];
	var x_intArray1D: DistArray[int];

	public def run(): boolean = {
		var start: long;
                var stop: long;
		val OneDSize: int = kArraySize * kArraySize;

		start = System.currentTimeMillis();
		x10.io.Console.OUT.println("creating array size "+OneDSize);
		x_javaArray = new Array[double](OneDSize);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created array in "+(((stop-start) as double)/1000)+" seconds");

		start = System.currentTimeMillis();
		x10.io.Console.OUT.println("creating dist array size "+OneDSize);
		val r = Region.make(0, OneDSize);
		val D = Dist.makeBlock(r);
		x_doubleArray1D = DistArray.make[double](D);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created array in "+(((stop-start) as double)/1000)+" seconds");

		x10.io.Console.OUT.println("creating array ["+kArraySize+","+kArraySize+"] ("+(kArraySize*kArraySize)+")");
		val r2 = Region.make(0..kArraySize, 0..kArraySize);
		val D2 = Dist.makeBlock(r2);
		x10.io.Console.OUT.println("Start allocation...");
		start = System.currentTimeMillis();
		x_doubleArray2D = DistArray.make[double](D2);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created array in "+(((stop-start) as double)/1000)+" seconds");
		x10.io.Console.OUT.println("finished allocating");

		start = System.currentTimeMillis();
		x_intArray1D = DistArray.make[int](D);
		stop = System.currentTimeMillis();
		x10.io.Console.OUT.println("Created int array in "+(((stop-start) as double)/1000)+" seconds");
		x10.io.Console.OUT.println("finished allocating");
		return true;
	}

	public static def main(Rail[String]):void {
		new Initialization().execute();
	}
}
