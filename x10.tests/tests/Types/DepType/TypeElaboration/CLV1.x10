/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
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
	 var f: CircularLV{k==3n} = new CircularLV(3n) as CircularLV{k==3n} ;
     }
 
 }
