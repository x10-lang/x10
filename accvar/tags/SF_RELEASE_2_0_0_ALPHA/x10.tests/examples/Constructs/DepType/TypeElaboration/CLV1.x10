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

 public class CLV1(i:int, j:int)  {
     public def this(i:int, j:int):CLV1{self.i==i, self.j==j} = {
	    property(i,j);
     }
     public def m():void = {
	 var f: CircularLV{k==3} = new CircularLV(3) as CircularLV{k==3} ;
     }
 
 }
