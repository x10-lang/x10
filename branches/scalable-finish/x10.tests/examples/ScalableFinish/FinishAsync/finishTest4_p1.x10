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


/**
 * Description: 
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
public class finishTest4_p1 {
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
    	 finish{
    	 at(here){
    		 async{}
    	 }
    	 async{
    		 at(here){}
    	 }
        }
        async{
                finish{
                        at(here){}
                }
                at(here){
                        finish{}
                        async{}
                }
        }
        at(here){
                finish{
                        async{}
                }
                async{
                        finish{
                                async{}
                        }
                }
        }

        at(here){
                async{}
                at(here){
                        async{}
                }
                f1();
        }
        async{
                async{
                        f2();
                }
        }
	}

	public static def main(args: Rail[String]) {
		new finishTest4_p1().run();
	}
}


         
