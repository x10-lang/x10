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

/**
 * Ensure that tabs in strings do not cause compilation errors.
 * @author vj
 * @author kemal 11/2005
 */
public class TabsInStrings extends x10Test {

	static class fmt {
		public static def format(var t: double): String { return ""+t; }
	}

	public def run(): boolean {
		var t: double = 25;
		var tmax: double = 200;
		x10.io.Console.OUT.println("	--> total mg-resid "+fmt.format(t)+
				" ("+fmt.format(t*100./tmax)+"%)");
		x10.io.Console.OUT.println("		Hello		world!		");
		return true;
	}

	public static def main(var args: Rail[String]): void {
		new TabsInStrings().execute();
	}
}
