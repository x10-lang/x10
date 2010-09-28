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
 * Testing that an at spawned at some other place cannot access a remote field.
 */
public class AtFieldWrite_MustFailCompile extends x10Test {
	private val root = GlobalRef[AtFieldWrite_MustFailCompile](this);
	transient var t: T;
    public def run() {
	    val Second = Place.FIRST_PLACE.next();
	    val newT = new T();
	    val root = this.root;
	    at (Second) { 
	    	// Note that  this statement will not be flagged as an error by the compiler
	    	this.t = newT;
	    	// It is a perfectly legal write to the local object implicitly created
	    	// across the place shift and bound to this.
	    	
	    	// The right way to try to assign to the transient t field of the original object is to use
	    	// root to access it. 
		    root().t = newT; 
		    // THe correct pattern should be at (root) root().t = (newT.root as {root.home==here})();
	    }
        return true;
    }

    public static def main(Array[String](1)) {
	    new AtFieldWrite_MustFailCompile().execute();
    }

    static class T {
    	private val root = GlobalRef[T](this);
	    transient public var i: int;
    }
}
