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
 * A struct cannot subclass a class.
 * @author vj
 */
public class StructCannotSubclassClass_MustFailCompile extends x10Test {

	class B {}
    struct A extends B { // ERR: Syntax error: Token "implements" expected instead of this input
	 val x:int=5;
    }

    public def run()=true;

    public static def main(Rail[String])  {
	new StructCannotSubclassClass_MustFailCompile().execute();
    }
}
