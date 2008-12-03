/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 /**  
 Check that circular dependencies between classes (through local variables) are handled correctly
 during TypeElaboration.
 
 *@author vj
 *
 */

import harness.x10Test;

 public class CircularLV(int k)  extends x10Test { 
  
	 public CircularLV(int k) { this.k = k}
	
	 public boolean  run() { 
		 CLV1(:i==j) h = (CLV1(:i==j)) new CLV1(4,4);
		 return true;
	 }
   
  public static void main(String[] args) {
	  new CircularLV(4).execute();
	}
 }