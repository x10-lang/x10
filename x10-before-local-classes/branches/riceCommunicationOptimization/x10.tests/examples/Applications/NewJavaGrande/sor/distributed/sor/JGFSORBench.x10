/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package sor;

import jgfutil.*;
import x10.util.Random;;

/**
 * X10 port of sor benchmark from Section 2 of Java Grande Forum Benchmark Suite.
 *
 *  PARALLEL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * @author vj (ported to X10 v1.7)
 */
public class JGFSORBench extends SOR implements JGFSection2 {

    private var size: Int;
    private val datasizes =  [10, 1500, 2000];
    private const JACOBI_NUM_ITER = 100;
    private const RANDOM_SEED  = 10101010L;

    val R  = new Random(RANDOM_SEED);

    public def JGFsetsize(size: Int) {
	this.size = size;
    }

    public def JGFinitialise() {
    }

    public def JGFkernel() {
	val G  = RandomMatrix(datasizes(size), datasizes(size), R);
	SORrun(1.25, G, JACOBI_NUM_ITER);
    }

    public def JGFvalidate() {
	//double refval[] = { 0.0012191583622038237D, 1.123010681492097D, 1.9967774998523777D };
	val refval = [ 4.5185971433257635E-5D, 1.123010681492097D, 1.9967774998523777D ];
	val dev = Math.abs(gTotal - refval(size));
	if (dev > 1.0e-12) {
	    Console.OUT.println("Validation failed");
	    Console.OUT.println("gTotal = " + gTotal + "  " + dev + "  " + size);
	    throw new Error("Validation failed");
	}
    }

    public def JGFtidyup() {
    }

    public def JGFrun(size: Int) {
	JGFInstrumentor.addTimer("Section2:SOR:Kernel", "Iterations", size);

	JGFsetsize(size);
	JGFinitialise();
	JGFkernel();
	JGFvalidate();
	JGFtidyup();

	JGFInstrumentor.addOpsToTimer("Section2:SOR:Kernel", (JACOBI_NUM_ITER) as Double);
	JGFInstrumentor.printTimer("Section2:SOR:Kernel");
    }

    private static def RandomMatrix(M: int, N: int, R: Random): Array[Double] {
	val t = Array.make[Double]([0..M-1, 0..N-1]);
	for ((i,j) in t.region) 
	    t(i, j) = R.nextDouble() * 1e-6;
	return t;
    }

    private static def write(t: Array[Double],  i: Int, j: Int, v: Double) {
		at(t.dist(i, j)) t(i, j) = v;
	}

	private static def blockStar(val r1: region, val r2: region{rank==1}): dist = {
		val d1: dist = distmakeBlock(r1);
		return distTimesRegion(d1, r2);
	}

	/**
	 * Takes the cartesian product of a 1D distribution d and
	 * 1D region r, retuning a new 2D distribution d*r.
	 *
	 * Region of (d*r) = [d.region,r]
	 *
	 * Point to place mapping of (d*r):
	 *
	 * For all i, For all j, (d*r)[i,j] = d[i]
	 */
	private static def distTimesRegion(d: Dist, r: Region(1)): dist {
	    var d0: dist{rank==2} = Dist.makeConstant([1..0, 1..0], here);
	    for (val p: place in d.places()) 
		d0 = d0 || (Dist.makeConstant([(d|p).region as Region(1), r], p);
	    return d0;
	}
}
