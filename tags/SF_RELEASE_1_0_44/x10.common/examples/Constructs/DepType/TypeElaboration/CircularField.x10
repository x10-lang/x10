/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 /**  
 Check that circular dependencies between classes (through fields) are handled correctly
 during TypeElaboration.
 
 *@author vj
 *
 */

import harness.x10Test;

 public class CircularField(int k)  extends x10Test { 
  
	 public CircularField(int k) { this.k = k}
	 CF1(:i==j) h = (CF1(:i==j)) new CF1(4,4);
	 public boolean  run() { 
		 return true;
	 }
   
  public static void main(String[] args) {
	  new CircularField(4).execute();
	}
 }