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
public class finishTest2_p2 {

        public def f1():void {
                // method contains async
                async(Place.place(10)){}
        }
        public def f2():void {
                // method contains at
                at(here){}
        } 
	public def run() {
		
                //TODO: test code
        
        var i:int = 0;

        // finish with multiple asyncs
        finish{
                async(here.next()){}
                async(Place.place(3)){}
        }

        finish{
                async(here.next()){}
                f1();
                async(here.next().next()){}
        }


        finish{
                at(here.next()){}
                f2();
                at(here){}
        }

        // finish with async and at
        finish{
                at(here){}
                i = i + 1;
                async(Place.place(2)){}
        }
        finish{
                async(here.next()){
                        f1();
                }
        }


        finish{
                async(here.next()){
                        f2();
                }
        }
	}

	public static def main(args: Rail[String]) {
		new finishTest2_p2().run();
	}
}


         
