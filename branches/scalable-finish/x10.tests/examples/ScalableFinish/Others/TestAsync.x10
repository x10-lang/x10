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

/**
 * problem: wala does not build foo into callgraph 
 */
 class Foo{
		public static def foo4(){}
		public def foo3(){}
	}
public class TestAsync{
	var flag: boolean = false;
    public static def foo2(){}
    public def foo() {}
    public def run() {
    	//TODO: test code
    	val f = new Foo();
    	async{
    	    // non-static member function
    	    foo();
    	    // static member function
    	    TestAsync.foo2();
    	    //non-static non-member function
    	    f.foo3();
    	    //static non-member function
    	    Foo.foo4();
    }
    //default successful condition
    var b: boolean = false;
    atomic { b = flag; }
    return b;
}
    
public static def main(args: Rail[String]) {
    	new TestAsync().run();
}
}
