/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package sor;

import jgfutil.*;
import x10.util.Random;
import x10.io.Console;
/**
 * X10 port of sor benchmark from Section 2 of Java Grande Forum Benchmark Suite.
 *
 *  SERIAL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 * @author vj (ported from X10 1.5)
 */
public class JGFSORBench extends SOR implements JGFSection2 {

	private var size: int;
	private val datasizes = [ 10, 1500, 2000 ];
	private const JACOBI_NUM_ITER = 100;
	private const RANDOM_SEED = 10101010L;

	val R = new Random(RANDOM_SEED);

	public def JGFsetsize(size: int) {
		this.size = size;
	}

	public def JGFinitialise() {
	}

	public def JGFkernel(){
		val G = RandomMatrix(datasizes(size), datasizes(size), R);
		SORrun(1.25, G, JACOBI_NUM_ITER);
	}

	public def JGFvalidate() {
		//double refval[] = { 0.0012191583622038237D, 1.123010681492097D, 1.9967774998523777D };
		val refval = [ 4.5185971433257635E-5D, 1.123010681492097D, 1.9967774998523777D];
		val dev = Math.abs(gTotal - refval(size));
		if (dev > 1.0e-12) {
		    Console.OUT.println("Validation failed");
		    Console.OUT.println("gTotal = " + gTotal + "  " + dev + "  " + size);
		    throw new Error("Validation failed");
		}
	}

	public def JGFtidyup() {
	    //		System.gc();
	}

	public def JGFrun(size: Int) {
		JGFInstrumentor.addTimer("Section2:SOR:Kernel", "Iterations", size);

		JGFsetsize(size);
		JGFinitialise();
		JGFkernel();
		JGFvalidate();
		JGFtidyup();

		JGFInstrumentor.addOpsToTimer("Section2:SOR:Kernel",  JACOBI_NUM_ITER as Double);
		JGFInstrumentor.printTimer("Section2:SOR:Kernel");
	}

	private static def RandomMatrix(M: Int, N: Int, R: Random): Array[Double] {
		val t = Array.make[Double]([0..M-1, 0..N-1]);
		for ((i,j) in t.region) 
		    t(i, j) = R.nextDouble() * 1e-6;
		return t;
	}
}
