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
/**
 * Description: 
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
public class TrivialTest3 extends x10Test {

	var flag: boolean = false;
	
        
        public def f1():void {
	
	var i:int = 1;
i++;
	//throw new IllegalOperationException();

}
        
        public def run() {
		
                //TODO: test code
      finish{
        	async{}
        }
      /*var f:boolean  = true;
      if(f){
    	  f1();
    	  throw new IllegalOperationException();
    	  //f1();
    	  }
      var i:int = 1;
    	  f1();
      i++;
      i = i + 3;*/
 
                //default successful condition
                var b: boolean = false;
		atomic { b = flag; }
		return b;
	}

	public static def main(args: Rail[String]) {
		new TrivialTest3().execute();
	}
}


         
