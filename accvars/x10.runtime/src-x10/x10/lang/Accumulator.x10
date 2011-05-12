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

	public property tempId():String = "x";
	
	private var curr:T;
	private val red:Reducible[T];
	public def this(red:Reducible[T]) {
		curr = red.zero();
		Console.OUT.println("Acc 1: "+curr);
		this.red = red;
	}
	
	public def supply(t:T) {
	Console.OUT.println("before value: "+curr);
		curr = Runtime.makeAccSupply(curr, red, t);
		Console.OUT.println("AFTER Acc supply: "+curr);
	}
	
	public def result():T {
			Console.OUT.println("Acc result: "+curr);

		return curr;
		
	}
}



