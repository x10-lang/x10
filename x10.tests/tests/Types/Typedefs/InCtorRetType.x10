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

import harness.x10Test;

/**
 * Test type defs in Ctor return type.
 *
 * @author vj 8/2008
 */
public class InCtorRetType extends x10Test {
    static type Testy(i:int) = Test{self.i==i};

    static class Test(i:int) {
       def this(i:int):Testy(i) = {
          property(i);
       }
    }
	public def run()=true;

	public static def main(var args: Rail[String]): void = {
		new InCtorRetType().execute();
	}
}

