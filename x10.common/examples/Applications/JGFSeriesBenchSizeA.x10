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


import series.*; 
import jgfutil.*;

public class JGFSeriesBenchSizeA{ 

  public static int nprocess;
  public static int rank;

  public boolean run(){


    if(rank==0) {
      JGFInstrumentor.printHeader(2,0,place.MAX_PLACES);
    }
    JGFSeriesBench se = new JGFSeriesBench(place.MAX_PLACES,rank); 
    se.JGFrun(0);
    return true;
 
  }
   /**
    * main method
    */
   public static void main(String args[]) {
		boolean b= (new JGFSeriesBenchSizeA()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
   }

}


