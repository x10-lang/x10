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
 * Description: 
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
public class TrivialTest2 extends x10Test {

	var flag: boolean = false;


  public def f1():int{
	  var i:int = 1;
  async{}
	  return i + 1;
  }
  
  
  public def bar():int{
	  return 2;
  }
  
  
  public def f5():int{
	  return 2;
  }
  
  
  public def f4():int{
	  return 2;
  }
  public def f6():void{
  }
  public def f3(x:Rail[int],y:Rail[int]):Rail[int]{
	  return x;
  }

	public def run() {
		
                //TODO: test code

                f4();
 //finish{
		//async{}
		//f4();
//		async{};
	//}
	  // var i:int = 1;
  // finish{
	  // f4();
	  // async{}
	  // at(here){async{}}
  // }
	 // f5();
   // f3([1,2,3],[1,2]);
   
	 // finish{
		  // f6(); 
		  // at(here){f1();}
		 // }
	  // async{
		  // at(here){}
		  // async{
			  // bar();
			  // at(here){}
		  // }
	  // }
	  // finish{
		 // at(here){f4();}
		 // finish{
			 // at(here){}
		 // }
	  // }
	  // at(here){
		  // at(here){f6();}
	  // }
                //default successful condition
                var b: boolean = false;
		atomic { b = flag; }
		return b;
	}

	public static def main(args: Rail[String]) {
		new TrivialTest2().execute();
	}
}


         
