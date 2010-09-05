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

package x10.array;

import x10.compiler.Global;
/**
 * A class implementing a global reference to an Array.
 */
public struct GlobalArrayRef[T](region:Region) {
	val root: GlobalRef[Array[T]];
    def this(a:Array[T]) {
    	property(a.region());
    	root = GlobalRef[Array[T]](a);
    }
    @Global public safe def equals(a:Any) {
    	if (a == null || !(a instanceof GlobalArrayRef[T]))
    		return false;
    	return (a as GlobalArrayRef[T]).root == this.root;
    }
    @Global public safe def hashCode()=root.hashCode();
    @Global public safe def home()=root.home;
    
}