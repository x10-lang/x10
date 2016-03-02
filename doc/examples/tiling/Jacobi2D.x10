/******************************************************************************
 * Jacobi2D benchmark
 * Basic parallelism over inner loop forall
 *
 *
 * Based in the Chapel code of Ian J. Bertolacci - CSU
 ******************************************************************************/

import x10.xrx.Runtime;
import x10.util.*;
import x10.array.*;

class Jacobi2D {

    public static def expand (space: DenseIterationSpace_2, i:Long) {
	val min0 = space.min0 - i;
	val max0 = space.max0 + i;
	val min1 = space.min1 - i;
	val max1 = space.max1 + i;
	return min0..max0 * min1..max1;
    }

    var printTime: Boolean = false;
    val globalSeed = 0; //SeedGenerator.currentTime;
    val problemSize = 1000;
    val T = 100; // number of time steps
    val tau = 30;
    var verify: Boolean = false;
    var benchmark: Boolean = true;


    // main
    // Given that this is a very straight forward benchark the code is almost
    // entirely kept within the main function.
    // The steps taken in this code are the following:
    // 2 - data allocation and initialization
    // 3 - jacobi 1D timed within an openmp loop
    // 4 - output and optional verification
    //
    public def main(){

	// 2 - data allocation and initialization
	val lowerBound = 1; // start of computation space
	val upperBound = problemSize ; // end of computation space

	// create a haloed domain
	val totalSpaceRange =  0..(problemSize+1) * 0..(problemSize+1);
	val computationTimeRange = 1..T;
	val computationDomain = expand(totalSpaceRange, -1);

	// Storage array.
	val space = new Array_3[Double](2, 1 + totalSpaceRange.max0 - totalSpaceRange.min0, 1 + totalSpaceRange.max1 - totalSpaceRange.min1, 0.0); // : [0..1, totalSpaceRange.dim(1), totalSpaceRange.dim(2) ] Cell;

	val timer = new Timer();

	// initialize space with values
	val generator = new Random( globalSeed );

	// forall (x,y) in computationDomain do{
	//     space[0, x, y] = 0;
	//     space[1, x, y] = 0;
	// }

	for ([x, y] in computationDomain) {
	    space(0, x, y) = generator.nextDouble();
	}

	// 3 - jacobi 1D timed within an openmp loop
	val start = timer.nanoTime();

	Tile.Diamond.for (read:Long, write:Long, x:Long ,y:Long
			      in new Tile.DiamondIterator(lowerBound, upperBound, T, tau)) {
	    space(write, x, y) = (space(read, x, y-1) +
				  space(read, x-1, y) +
				  space(read, x, y+1) +
				  space(read, x+1, y) +
				  space(read, x, y) ) / 5;
	}

	//read <=> write;

	val time = timer.nanoTime() - start;

	// 4 - output and optional verification
	if (printTime) {
	    Console.OUT.println( "Diamond Time: "+ time / 1000000000.0 );
	}
	if (verify) {
	    if (verifyResult(space, computationDomain, false, T )) {
		Console.OUT.println( "SUCCESS" );
	    } else {
		Console.OUT.println( "FAILURE" );
	    }
	}
	if (benchmark) {
	    Console.OUT.println(Runtime.NTHREADS+","+ time / 1000000000.0);
	}
    }


    // return true if the current end state is the same as the
    // stencil applied to the original state, in serial iteration.
    public def verifyResult(space: Array_3[Double], computationalDomain: DenseIterationSpace_2, verbose: Boolean, T: Long ): Boolean {

	val computationTimeRange = 1..T;

	val spaceEndState = new Array_2[Double](space.numElems_2, space.numElems_3, 0.0);//: [computationalDomain] Cell;

	for ([x, y] in computationalDomain) {
	    spaceEndState( x, y ) = space( T & 1, x, y );
	}

	val generator = new Random( globalSeed );

	for ([x, y] in computationalDomain) {
	    space(0, x, y) = generator.nextDouble();
	}

	val timer = new Timer();
	val start = timer.nanoTime();

	var read: Long = 0;
	var write: Long = 1;

	for (t in computationTimeRange) {
	    for ([x,y] in computationalDomain) {
		space(write, x, y) = (space(read, x, y-1) + space(read, x-1, y) +
				      space(read, x, y+1) + space(read, x+1, y) +
				      space(read, x, y) ) / 5;
	    }
	    val tmp = read;
	    read = write;
	    write = tmp;
	}

	val time = timer.nanoTime() - start;
	if (printTime) { Console.OUT.println( "Sequential Time: "+ time / 1000000000.0); }

	var passed: Boolean = true;

	for ([x, y] in computationalDomain) {
	    if (spaceEndState(x,y) != space(T & 1, x, y))  {
		if (verbose) {
		    Console.OUT.println( "FAILED! "+spaceEndState(x,y)+" != "+space( T & 1, x, y )+" at "+x+ ", "+y );
		}
		passed = false;
		break;
	    }
	}

	if (passed && verbose) {
	    Console.OUT.println( "SUCCESS!" );
	}

	return passed;

    }

    public static def main(Rail[String]) {
	val j = new Jacobi2D();
	j.main();
    }

}
