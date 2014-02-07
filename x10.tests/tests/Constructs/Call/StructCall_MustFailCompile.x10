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
 * Checking that constructor calls check for number of args.
 */
public class StructCall_MustFailCompile extends x10Test  {
    static struct S1 {
      val x:int;
      val y:int;

      public def this(a:int, b:int) { x = a; y = b; }

      public final def sum() = x + y;
    }

	public def run():boolean {
		  val a:S1 = S1(1n); // ERR: Method or static constructor not found for given call.	 Call: S1(x10.lang.Int{self==1})
          return true;
	}
	public static def main(var args: Rail[String]): void = {
		new StructCall_MustFailCompile().execute();
	}
}
