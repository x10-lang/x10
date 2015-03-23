/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.util.concurrent.Future;
/**
 * A Future.make expression cannot occur in a when.
 * @vj
 */
public class NoFutureMakeInWhen extends x10Test {
	
	public def run(): boolean {
	   try {
		 val b=true;
	     when (b==true) {
		    val x = Future.make[int](()=>0n);
	     }
	   } catch (IllegalOperationException) {
		   return true;
	   }
	   return false;
	}

	public static def main(Rail[String]){
		new NoFutureMakeInWhen().execute();
	}
}
