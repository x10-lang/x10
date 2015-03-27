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
 Check that circular dependencies between classes (through fields) are handled correctly
 during TypeElaboration.
 
 *@author vj
 *
 */

import harness.x10Test;

 public class CircularField(k:int)  extends x10Test { 
  
     public def this(k:int):CircularField { property(k);}
     val h = new CircularField1(4n,4n) as CircularField1{i==j} ;
     public  def run():boolean = true;
   
     public static def main(Rail[String]) {
	 new CircularField(4n).execute();
     }
 }
