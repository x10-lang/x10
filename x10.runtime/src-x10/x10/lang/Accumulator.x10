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

package x10.lang;

/**
 * Accumulator object is used for todo... 
 * 
 *
 * @author Nate 04/09/11
 */
public final class Accumulator[T] {
	var curr:T;
	val red:Reducible[T];
	var isWriteOnly:Boolean = false;
	def this(red:Reducible[T]) {
		curr = red.zero();
		this.red = red;
	}
	def supply(t:T) {
		atomic curr = red(curr,t);
	}
	def result():T {
		if (isWriteOnly) throw new IllegalOperationException();
		return curr;
	}
	def setWriteOnly() {
		isWriteOnly = true;
	}
	def setReadWrite() {
		isWriteOnly = false;
	}
	def isWriteOnly() = isWriteOnly;
}



