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
import x10.regionarray.*;

/**
 * @author vj
 */
public class StaticReturn extends x10Test {

	public def run(): boolean = {
		var s: Dist{rank==2} = starY();
		return true;
	}
	def starY(): Dist{rank==2} = {
		var d: Dist{rank==2} = Dist.makeConstant(Region.make(0..1, 0..1), here);
		return d;
	}
	public static def main(Rail[String])  {
		new StaticReturn().execute();
	}
}
