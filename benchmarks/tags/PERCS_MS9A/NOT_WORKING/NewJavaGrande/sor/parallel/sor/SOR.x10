/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package sor;

import jgfutil.*;
import x10.lang.Double;

/**
 * X10 port of sor benchmark from Section 2 of Java Grande Forum Benchmark Suite.
 *
 *  SERIAL VERSION
 *
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
public class SOR {
    var gTotal:Double = 0.0D;
    final public def SORrun(omega: Double, G: Array[Double], numIter: Int) {
	val M = G.region().max(0);
	val N = G.region().max(1);
	
	val omega_over_four  = omega * 0.25;
	val one_minus_omega = 1.0 - omega;

	 // update interior Points
	 //
	 JGFInstrumentor.startTimer("Section2:SOR:Kernel");

	 for ((p) in 1..numIter) 
	     for ((o) in 0..1) 
		 finish foreach ((ii) in 0..(((M-2-(1+o))/2))) {
		 val i = 2 * ii + 1 + o;
		 for ((j) in 1..N-2) 
		     G(i, j) = omega_over_four * (G(i-1, j) + G(i+1, j) + G(i, j-1)
						  + G(i, j+1)) + one_minus_omega * G(i, j);
	     }

	 JGFInstrumentor.stopTimer("Section2:SOR:Kernel");
	 gTotal = G.reduce(Double.+, 0);
    }
}
