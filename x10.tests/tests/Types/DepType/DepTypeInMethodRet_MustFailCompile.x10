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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;
import x10.compiler.*; // @Uncounted @NonEscaping @NoThisAccess
import x10.compiler.tests.*; // err markers
import x10.util.*;

/**
 * Cannot return a value of type boolean from a method whose return type is boolean(:self==true).
 *
 * @author vj
 */
public class DepTypeInMethodRet_MustFailCompile extends x10Test {
    
    @ERR public def m(var t: boolean): boolean(true) = t; 
	public def run()=m(false);
	public static def main(var args: Rail[String]): void = {
		new DepTypeInMethodRet_MustFailCompile().execute();
	}
}
