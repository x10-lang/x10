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
		System.out.println("creating java array size "+OneDSize);
		x_javaArray = new Array[double](OneDSize);
		stop = System.currentTimeMillis();
		System.out.println("Created array in "+(((stop-start) to double)/1000)+" seconds");

		start = System.currentTimeMillis();
		System.out.println("creating array size "+OneDSize);
		var r: region = [0..OneDSize];
		val D: dist = dist.makeBlock(r);
		x_doubleArray1D = new Array[double](D);
		stop = System.currentTimeMillis();
		System.out.println("Created array in "+(((stop-start) to double)/1000)+" seconds");

		System.out.println("creating array ["+kArraySize+","+kArraySize+"] ("+(kArraySize*kArraySize)+")");
		var r2: region = [0..kArraySize, 0..kArraySize];
		val D2: dist = dist.makeBlock(r2);
		System.out.println("Start allocation...");
		start = System.currentTimeMillis();
		x_doubleArray2D = new Array[double](D2);
		stop = System.currentTimeMillis();
		System.out.println("Created array in "+(((stop-start) to double)/1000)+" seconds");
		System.out.println("finished allocating");

		start = System.currentTimeMillis();
		x_intArray1D = new Array[int](D);
		stop = System.currentTimeMillis();
		System.out.println("Created int array in "+(((stop-start) to double)/1000)+" seconds");
		System.out.println("finished allocating");
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Initialization().execute();
	}
}
