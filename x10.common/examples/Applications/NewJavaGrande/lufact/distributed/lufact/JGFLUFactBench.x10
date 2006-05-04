/**************************************************************************
 *                                                                         *
 *             Java Grande Forum Benchmark Suite - Version 2.0             *
 *                                                                         *
 *                            produced by                                  *
 *                                                                         *
 *                  Java Grande Benchmarking Project                       *
 *                                                                         *
 *                                at                                       *
 *                                                                         *
 *                Edinburgh Parallel Computing Centre                      *
 *                                                                         * 
 *                email: epcc-javagrande@epcc.ed.ac.uk                     *
 *                                                                         *
 *                                                                         *
 *      This version copyright (c) The University of Edinburgh, 1999.      *
 *                         All rights reserved.                            *
 *                                                                         *
 **************************************************************************/

/**
 * @author ahk,cmd
 * Ported to x10 March 18th 2005
 */
package lufact; 
import jgfutil.*; 

public class JGFLUFactBench extends Linpack implements JGFSection2{	
	
	private int size;
	//private int datasizes[] = {150,1000,2000};
	private int datasizes[] = {50,1000,2000};
	public void JGFsetsize(int size){
		this.size = size;
	}
	
	public void JGFinitialise(){
		n = datasizes[size]; 
		System.out.println("ATTENTION: Running with smaller size (" + n + " instead of 500)");
		ldaa = n; 
		lda = ldaa + 1;
		
		region vectorRegion = [0: (ldaa-1)];
		region rectangularRegion = [0:(ldaa-1), 0:(lda-1)];
		region slimRegion = [0:0, 0:(lda-1)];
		
		dist rectangular_distribution = dist.factory.blockCyclic(rectangularRegion,lda);
		dist vector_distribution = dist.factory.cyclic(vectorRegion);
		dist slim_distribution = dist.factory.cyclic(slimRegion);
		
		a = new double[rectangular_distribution];
		//b[j],x[j], ipvt[j] same place as a[j, *], which balance load better
		//except for b[n], x[n]
		b = new double [slim_distribution]; 
		x = new double [slim_distribution];
		ipvt = new int [vector_distribution]; 
		
		
		long nl = (long) n;   //avoid integer overflow
		ops = (2.0*(nl*nl*nl))/3.0 + 2.0*(nl*nl);
		
		norma = matgen(a,lda,n,b);
	}
	
	public void JGFkernel(){
		
		JGFInstrumentor.startTimer("Section2:LUFact:Kernel");  
		info = dgefa(a,lda,n,ipvt);
		dgesl(a,lda,n,ipvt,b,0);
		JGFInstrumentor.stopTimer("Section2:LUFact:Kernel"); 
		
	}
	
	public void JGFvalidate(){
		
		int i;
		double eps,residn;
		final double ref[] = {6.0, 12.0, 20.0}; 
		
		final double[.] xx = x;
		final double[.] bb = b;
		finish ateach(point [_]: uniqueD) {
			for (point [i,j]: bb.distribution|here) 
				xx[i,j] = bb[i,j];
		}
		norma = matgen(a,lda,n,b);
		
		finish ateach(point [_]:uniqueD) {
			for (point [i,j]: bb.distribution|here)
				bb[i,j] = -bb[i,j];
		}
		
		dmxpy(n,b,n,lda,x,a);
		resid = 0.0;
		normx = 0.0;
		for (i = 0; i < n; i++) {
			double dtmp1 = abs(read(b, 0,i));
			double dtmp2 = abs(read(x, 0,i));
			resid = (resid > dtmp1) ? resid : dtmp1;
			normx = (normx > dtmp2) ? normx : dtmp2;
		}
		
		eps =  epslon((double)1.0);
		residn = resid/( n*norma*normx*eps );
		
		if (residn > ref[size]) {
			System.out.println("Validation failed");
			System.out.println("Computed Norm Res = " + residn);
			System.out.println("Reference Norm Res = " + ref[size]); 
			throw new Error("Validation failed");
		}
		
	}
	
	public void JGFtidyup(){
		// Make sure large arrays are gc'd.
		
		
		/* CMD
		 * this causes problems in X10, and strictly spreaking, is
		 * unnecessary
		 
		 a = null; 
		 b = null;
		 x = null;
		 ipvt = null; 
		 System.gc(); 
		 */ 
	}
	
	
	public void JGFrun(int size){
		
		
		JGFInstrumentor.addTimer("Section2:LUFact:Kernel", "Mflops",size);
		
		
		JGFsetsize(size); 
		JGFinitialise(); 
		JGFkernel(); 
		JGFvalidate(); 
		JGFtidyup(); 
		
		
		JGFInstrumentor.addOpsToTimer("Section2:LUFact:Kernel", ops/1.0e06);
		JGFInstrumentor.printTimer("Section2:LUFact:Kernel"); 
	}
	
}
