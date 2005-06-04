/**
 * Multi-place moldyn ported to x10.
 *
 * Uses the original MPI based algorithm but within a
 * multi-place x10 ateach loop
 *
 * @author kemal 3/2005
 */
/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
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


import moldyn.*;
import jgfutil.*;

public class JGFMolDynBenchSizeA{ 

  public static void main(String argv[]) {

    final int nprocess=place.MAX_PLACES;
    JGFInstrumentor.printHeader(3,0,nprocess);
    JGFMolDynBench mold = new JGFMolDynBench(); 
    mold.JGFrun(0);
 
  }
}


