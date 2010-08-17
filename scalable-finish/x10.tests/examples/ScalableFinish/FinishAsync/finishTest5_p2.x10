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
public class finishTest5_p2 {

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

        var i:int = 1;
        finish{
                if(i>1){
                        async{}
                }
                else{
                        at(here){}
                }
                f1();
        }

        if(i<1){
                finish{
                        at(here){}
                        async{}
                }

                async{}
                at(here){}
        }
        
        finish{
                async{
                        var i:int =1;
                        if(i==0){
                                async{}
                        }
                        if(i==1){
                                at(here){}
                        }
                }
        }
        finish{
                at(here){
        
                        var i:int =1;
                        if(i==2){
                                at(here){
                        
                                       async{}
                                }
                                async{}
                        }
                        at(here){}

                }
        }
	}

	public static def main(args: Rail[String]) {
		new finishTest5_p2().run();
	}
}


         
