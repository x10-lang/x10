/**************************************************************************
* Made Monte Carlo benchmark a test case
*
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


import montecarlo.*;

import jgfutil.*;

public class JGFMonteCarloBenchSizeA{ 

  public boolean run(){

    JGFInstrumentor.printHeader(3,0);

    JGFMonteCarloBench mc = new JGFMonteCarloBench(); 
    mc.JGFrun(0);
    return true;
 
  }
  public static void main(String args[]) {
	boolean b= (new JGFMonteCarloBenchSizeA()).run();
	System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
	System.exit(b?0:1);
  }
}
 
