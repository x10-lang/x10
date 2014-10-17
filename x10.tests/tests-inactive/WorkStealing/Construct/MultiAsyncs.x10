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
//OPTIONS: -WORK_STEALING=true

import harness.x10Test;
/*
 * Multiple asyncs
 */
public class MultiAsyncs extends x10Test{
	
	var value:int;
	
	public def run():boolean {
		finish {
		    async atomic this.value++;
	        async atomic this.value++;
	        async atomic this.value++;
		}

		Console.OUT.println("MultiAsyncs: value = " + this.value);
		return value == 3n;
	}

	public static def main(Rail[String]) {
        new MultiAsyncs().execute();
	}
}