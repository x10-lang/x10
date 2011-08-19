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

import harness.x10Test;

/**
 * (here).id compiled properly but here.id did not.
 * (as of 5/28/2005)
 *
 * @author kemal, 5/2005
 */
public class HereParentheses extends x10Test {

	public def run(): boolean = {
		x10.io.Console.OUT.println("(here).id="+(here).id+" (here).next()="+(here).next()
				   + " (here).prev()="+(here).prev());
		x10.io.Console.OUT.println("here.id="+here.id+ " here.next()=" 
				   + here.next()+" here.prev()"+here.prev());
		return true;
	}

	public static def main(var args: Array[String](1)): void = {
		new HereParentheses().execute();
	}
}
