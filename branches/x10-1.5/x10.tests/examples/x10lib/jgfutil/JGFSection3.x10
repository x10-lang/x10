/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package jgfutil;

/**
  * X10 port of montecarlo benchmark from Section 2 of Java Grande Forum Benchmark Suite (Version 2.0)
  *
  * @author Vivek Sarkar (vsarkar@us.ibm.com)
  *
  * Porting issues identified:
  * 1) Are there any differences between Java interfaces and X10 interfaces?
  */
 public interface JGFSection3 {
 public def JGFsetsize(size: int): void;
 public def JGFinitialise(): void;;
 public def JGFapplication(): void;
  public def JGFvalidate(): void;
  public def JGFtidyup(): void;  
  public def JGFrun(size: Int): void; 
}
