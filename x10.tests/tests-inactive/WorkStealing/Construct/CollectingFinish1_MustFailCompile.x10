/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
//OPTIONS: -WORK_STEALING=true
/*
 * Collecting-Finish. Cannot pass WS compile.
 */

public class CollectingFinish1_MustFailCompile {
	
    static struct Reducer implements Reducible[Int] {
     	public def zero()=0;
     	public operator this(a:Int,b:Int)=a+b;
    }
    
	public def run() {
		val value = finish (Reducer()){
			offer 3;
			async offer 3;
		};
		
		Console.OUT.println("CollectingFinish1: value = " + value);
		return 6==value;
	}

	public static def main(Rail[String]) {
        val r = new CollectingFinish1_MustFailCompile().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
	}
}