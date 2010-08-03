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

import harness.x10Test;

/**
 * Description: This program tests simple finish: a finish only contains a single 
 * async or at or method call.
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
public class finishTest1 extends x10Test {
	 var flag: boolean = false;
     public def f1():void {
    	 // method contains async
    	 async{}
     }
     public def f2():void {
    	 // method contains at
    	 at(here){}
     } 
     public def run() {
	
    	 //TODO: test code
    	 var i:int = 0;
    	 // finish without any statement
    	 finish{
    		 
    	 }
    	 // finish with single async
    	 finish{
    		 async{}
    	 }
    	 // finish with single at
    	 finish{
    		 at(here){}
    	 }
    	 
    	 // finish with other statements
    	 finish{
    		 i = i + 1;
    	 }
    	 
    	 // finish with method call
    	 finish{
    		 f1();
    	 }
    	 finish{
    		 f2();
    	 }
    	 
    	 //default successful condition
    	 var b: boolean = false;
    	 atomic { b = flag; }
    	 return b;
     }
     
     public static def main(args: Rail[String]) {
    	 new finishTest1().execute();
     }
 }



