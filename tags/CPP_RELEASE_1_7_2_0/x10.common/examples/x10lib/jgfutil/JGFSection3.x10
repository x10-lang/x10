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
  public void JGFsetsize(int size);
  public void JGFinitialise();
  public void JGFapplication();
  public void JGFvalidate();
  public void JGFtidyup();  
  public void JGFrun(int size); 
}
 
