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
import x10.compiler.*;
import x10.lang.*;

/**
 * Description: 
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
public class TrivialTest3 extends x10Test {
	 var flag: boolean = false;
 public def f1(args: Rail[String]){
	 var i:int = 1;
 i++;
 //throw new IllegalOperationException();
 }
 public def f2() {}
 
 public def f3(){
	 finish{
		 f4();
	 }
 }
 
 public def f4(){
	 async{
		 f3();
	 }
 }
 public def run() {
		
	 //TODO: test code
	 
	 
	 //val x = this;
	 //val body: ()=>Void = ()=>x.f2();
	 //finish{
		 //   body();
	 //}
	 
	 
	 
	 // try{
		// f2();
	 // }
	 // catch(e:Exception ){
		 // Console.OUT.println("2");
	 // }
	 
	 
	 finish{
		   async{}
		   async(here.next()){}
	 }
	 /*finish{
	 async(here.next()){}
 }*/
 //    val s = Rail.make[String](0);
 // f1(s);
 /*var f:boolean  = true;
  * if(f){
	  * f1();
	  * throw new IllegalOperationException();
	  * //f1();
  * }
  * var i:int = 1;
  * f1();
  * i++;
  * i = i + 3;*/
  
  //default successful condition
  var b: boolean = false;
  atomic { b = flag; }
  return b;
	}

	public static def main(args: Rail[String]) {
		new TrivialTest3().execute();
	}
}


         
