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


import sor.*; 
import jgfutil.*; 

public class JGFSORBenchSizeA{ 

  public boolean run(){

    JGFInstrumentor.printHeader(2,0);

    JGFSORBench sor = new JGFSORBench(); 
    sor.JGFrun(0);
    return true;
 
  }
   /**
    * main method
    */
   public static void main(String args[]) {
		boolean b= (new JGFSORBenchSizeA()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
   }

}
