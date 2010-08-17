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
class Foo{
	public static def foo4(){}
	public def foo3(){}
}
public class TestClosure /*extends x10Test*/ {
	var flag: boolean = false;
    public def foo() {}
    public static def foo2(){}
    public def run() {
    	//TODO: test code
    	val f = new Foo();
    	val body:()=>Void = ()=>{
    		// non-static member function
    		foo();
    		// static member function
    		TestClosure.foo2();
    		// non-static non-member function
    		f.foo3();
    		//static non-member function
    		Foo.foo4();
    		};
    	body();
        //default successful condition
        var b: boolean = false;
        //atomic { b = flag; }
        return b;
    }
    
    public static def main(args: Rail[String]) {
    	new TestClosure().run();
    }
}
