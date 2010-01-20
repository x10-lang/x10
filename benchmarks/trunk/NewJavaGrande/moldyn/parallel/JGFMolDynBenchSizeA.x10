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
import harness.x10Test;

public class JGFMolDynBenchSizeA extends x10Test {
   public def run() {
     val size = 0;
     JGFInstrumentor.printHeader(3, size, JGFMolDynBench.NTHREADS);
     val mold = new JGFMolDynBench(size);
     mold.JGFrun(size);
     return true;
   }

   public static def main(args: Rail[String]!) {
     new JGFMolDynBenchSizeA().execute();
   }
}
