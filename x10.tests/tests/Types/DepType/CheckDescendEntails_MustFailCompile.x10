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
 * Check that x=y implies x.f=y.f.
 *
 * @author vj
 */
public class CheckDescendEntails_MustFailCompile extends x10Test {
	
    class Test(a:Prop, b:Prop) {
        public def this(val a: Prop, val b: Prop): Test{self.a==a&&self.b==b} = { 
        	property(a,b);
        }
    }
    public def run(): boolean = {
	val p = new Prop(1n,2n);
		
	var t: Test{self.a == self.b} = new Test(p,p);
	@ERR var u: Test{self.a.i == self.b.j} = t; // this should fail type check.
	return true;
    }
    public static def main(var args: Rail[String]): void = {
	new CheckDescendEntails_MustFailCompile().execute();
    }
}
