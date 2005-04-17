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


import crypt.*;
import jgfutil.*; 

public class JGFCryptBenchSizeA{ 

  public static int nthreads;

  public static void main(String argv[]){

  if (argv.length > 1) {
    nthreads = Integer.parseInt(argv[1]);
  } else {
    System.out.println("The no of threads has not been specified, defaulting to 4");
    System.out.println("  ");
    nthreads = 4;
  }

    JGFInstrumentor.printHeader(2,0,nthreads);

    JGFCryptBench cb = new JGFCryptBench(nthreads); 
    cb.JGFrun(0);
 
  }
}


