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

 public class CircularField(k:int)  extends x10Test { 
  
     public def this(k:int):CircularField = { property(k);}
     val h = new CF1(4,4) as CF1{i==j} ;
     public  def run():boolean = true;
   
     public static def main(Rail[String]) = {
	 new CircularField(4).execute();
     }
 }
