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
public class finishTest7_p2  {

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
        var i:int = 1;
        finish{
        	for(i=1;i<10;i++){
        		for(i=1;i<10;i++){
        			async{}
        			if(i==3) break;
        		}
        		f1();
        	}
        	for(i=1;i<10;i++){
        		for(i=1;i<10;i++){
        			async{}
        			if(i==3) continue;	
        		}
        		
        	}
        	f1();
        }
        for(i=1;i<10;i++){
		 finish{
			 for(i=1;i<10;i++){
				 async{}
			 }
		 }
        }
        finish{
		 for(i=1;i<10;i++){
			 async{}
		 }
		
	 }

	}

	public static def main(args: Rail[String]) {
		new finishTest7_p2().run();
	}
}


         
