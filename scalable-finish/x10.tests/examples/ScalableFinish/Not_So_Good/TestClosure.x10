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

import x10.compiler.*;
import x10.lang.*;

/**
 * problem: wala does not build foo into callgraph 
 */

public class TestClosure /*extends x10Test*/ {
	var flag: boolean = false;
    public def foo() {}
    public def run() {
    	//TODO: test code
    	val body:()=>Void = ()=>foo();
    	body();
        //default successful condition
        var b: boolean = false;
        atomic { b = flag; }
        return b;
    }
    
    public static def main(args: Rail[String]) {
    	new TestClosure().run();
    }
}
