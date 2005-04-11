/**************************************************************************
*                                                                         *
*         Java Grande Forum Benchmark Suite - Thread Version 1.0          *
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
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/


import sparsematmult.*; 
import jgfutil.*; 

public class JGFSparseMatmultBenchSizeA{ 

    public boolean run() {
	
	int nthreads = place.MAX_PLACES;
	JGFInstrumentor.printHeader(2,0);

	JGFSparseMatmultBench smm = new JGFSparseMatmultBench(); 
	smm.JGFrun(0);
	return true;//came here in case no exception was thrown
 
    }
   public static void main(String[] args) {
	boolean b= (new JGFSparseMatmultBenchSizeA()).run();
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
  }
}
