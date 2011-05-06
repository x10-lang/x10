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

import x10.util.Random;

/**
 * Accumulator object is used for todo... 
 * 
 *
 * 
 */
public final class Accumulator[T] {
	
	private var curr:T;
	private var id:long;
	private val red:Reducible[T];
	
	public def this(red:Reducible[T]) {
		curr = red.zero();
		this.red = red;
		val randObj = new Random();
		id = randObj.nextLong();
	}
	
	public def supply(t:T) {
	
		Console.OUT.println("Supplying value :" +t);
		Runtime.makeAccSupply(id, t, red);
		
	}
	
	public def result():T {
			
			curr = Runtime.getAccValue[T](id, red);
			Console.OUT.println("Acc result: "+curr);
			return curr;
			
	}
	
	public def getId():long{
		return id;
	}
	
	

}



