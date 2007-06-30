/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 /**  
 Check that circular dependencies between classes are handled correctly
 during TypeElaboration.
 See CircularField.x10
 
 *@author vj
 *
 */



 public class CLV1(int i, int j)  {
  
	 public CLV1(int i, int j) {this.i=i; this.j=j;}
	 public void m() {
	 CircularLV(:k==3) f = (CircularLV(:k==3)) new CircularLV(3);
	 }
 
 }
  