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
   The compiler should generate an error if an attempt is made to assign a value to a variable
   whose type is inconsistent.
   @author vj
 */

public class EmptyType_MustFailCompile extends x10Test {

	
	class C(a:boolean) {
		val x:C{true&&false} = new C(true); // ERR ERR
	}
		
    public def run() = true;

    public static def main(Rail[String])  {
        new EmptyType_MustFailCompile().execute();
    }
}
