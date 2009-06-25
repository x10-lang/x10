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

 public class CF1(i:int, j:int)  {
     public def this(i:int, j:int):CF1{self.i==i,self.j==j} {
	    property(i,j);
     }
     var f:CircularField{k==3} = null;
 }
