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

package WorkStealing.Construct;

/*
 * Async Here, can be compiled in WS async
 */
public class AsyncHere {
	
	var flag: boolean = false;
	
	public def run() {
		finish {
			async { atomic { this.flag = true; } }
		}
		
		Console.OUT.println("AsyncHere: flag = " + this.flag);
		return flag;
	}

	public static def main(Array[String](1)) {
        val r = new AsyncHere().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
	
	
}