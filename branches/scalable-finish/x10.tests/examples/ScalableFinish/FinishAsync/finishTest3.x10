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
public class finishTest3 extends x10Test {

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
        // nested finish without statements
        finish{
                finish{
                }
        }

        // finish1 should only have async2 
        finish{// finish1
                
                finish{
                        //async1
                        async{}               
                }
                //async2
                async{}
        }

        finish{
                async{
                        finish{
                                async{}
                        }
                        f1();
                }
        }


        finish{                
                finish{
                        at(here){}               
                }
                at(here){}
        }

        finish{
                at(here){
                        finish{
                                at(here){}
                        }
                        f2();
                }
        }
        finish{
			 at(here){
			 finish{
				 async{}
				 
					 at(here){
						 async{}
					 }
				 
			 }
			 
		 }
			 async{}
	}
                //default successful condition
                var b: boolean = false;
		atomic { b = flag; }
		return b;
        }

	public static def main(args: Rail[String]) {
		new finishTest3().execute();
	}
}


         
