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
public class finishTest3_p2 {
    public def f1():void {
    	// method contains async
    	async{}
    }
    public def f2():void {
    	// method contains at
    	async(here.next()){}
        } 
    public def run() {
    	
    	//TODO: test code
    	
    	var i:int = 0;
    	// nested finish without statements
    	finish{
    		finish{
                        f2();
    		}
    	}
    	
    	// finish1 should only have async2 
    	finish{// finish1
    		finish{
    		//async1
    		async{
                        f2();
                }               
    	    }
    	//async2
    	async{}
    	}
    	
    	finish{
    		async{
    			finish{
    				f2();
    			}
    			f1();
    		}
    	}
    	
    	
    	finish{                
                finish{
                	f1();             
                }
                async(here){
                        f2();
                }
    	}
    	
        finish{
        	async(here){
        			async{}
        			
					 async(here){
						 f2();
					 }
					 
        		
		 }
        	async{}
        }
    }
    
	public static def main(args: Rail[String]) {
		new finishTest3_p2().run();
	}
 }


         
