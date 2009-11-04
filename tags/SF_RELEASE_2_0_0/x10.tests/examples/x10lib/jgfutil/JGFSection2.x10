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


package jgfutil; 

public interface JGFSection2 {
  public def JGFsetsize(size: Int): void;;
  public def JGFinitialise(): void;;
  public def JGFkernel(): void;;
  public def JGFvalidate(): void;;
  public def JGFtidyup(): void;;  
  public def JGFrun(size: Int): void;; 
}
