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
 * Test type parameter bounds.
 *
 * @author nystrom 8/2008
 */
public class Bounds7 extends x10Test {
      
        interface Sum {
          def sum():long;
        }

        public class Test[T]{T <: Sum} {
            def sum(a:T) = a.sum();

        }

	public def run() = true;
	public static def main( Rail[String]) {
		new Bounds7().execute();
	}
}


