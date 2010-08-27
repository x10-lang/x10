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
 * A struct cannot have a var field.
 * @author vj
 */
public class StructCannotHaveVarField_MustFailCompile extends x10Test {

    struct A {
	 var x:int=5;
    }

    public def run()=true;

    public static def main(Rail[String])  {
	new StructCannotHaveVarField_MustFailCompile().execute();
    }
}
