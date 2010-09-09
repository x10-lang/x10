/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
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
 public def JGFinitialise(): void;
 public def JGFapplication(): void;
  public def JGFvalidate(): void;
  public def JGFtidyup(): void;  
  public def JGFrun(size: Int): void; 
}
