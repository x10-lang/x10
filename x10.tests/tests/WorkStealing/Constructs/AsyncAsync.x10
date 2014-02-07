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
 * Nested async in local
 */
public class AsyncAsync extends x10Test{
	
	var value:long;
	
	public def run():boolean {
		finish {
			async {
				async {
					atomic this.value = this.value + 1;
				}
				atomic this.value = this.value + 2;
			}
			atomic this.value = this.value + 3;
		}

		Console.OUT.println("AsyncAsync: value = " + this.value);
		return value == 6;
	}

	public static def main(Rail[String]) {
        new AsyncAsync().execute();
	}
}