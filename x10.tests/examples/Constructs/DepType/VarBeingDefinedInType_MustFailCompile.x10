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
 * Check that the variable being defined can be used in the depclause of its type.
 *
 * @author vj
 */
public class VarBeingDefinedInType_MustFailCompile extends x10Test {
    
	public def run(): boolean = {
		var v: int{v ==0} = 0;
		var w: int{w==v} = 0; // ERR: cannot reference v in deptype, v is not final.
	    return v==0;
	}
	public static def main(Array[String](1)): void = {
		new VarBeingDefinedInType_MustFailCompile().execute();
	}
}
