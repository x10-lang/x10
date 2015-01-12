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

package x10.matrix.util;

/**
 * Provides some static math methods.
 */
public class MathTool {
	public static delta:Double = 0.00000001;

	/**
	 * Return true if the difference between the two values in Double 
	 * is less than delta, otherwise false.
	 */
	public static  def equals(a:Double, b:Double) = (Math.abs(a-b) < delta);
	public static  def equal(a:Double, b:Double) = (Math.abs(a-b) < delta);
	
	/**
	 * Return true if the value in Double type is less than delta
	 */
	public static  def isZero(a:Double)        = (Math.abs(a) < delta);

	/**
	 * Return true if the difference beween 1.0 and the value in Double 
	 * type is less than delta.
	 */
	public static  def isOne(a:Double)         = (Math.abs(a-1.0) < delta);

	/**
	 * Return an integer value which is no bigger than the square root of the specified integer, and
	 * can is evenly divisble.
	 */
	public static def sqrt(n:Int):Int {
		var rt:Int = Math.sqrt(n) as Int;
		for (; rt > 1n && n%rt != 0n; rt--);
		return rt;
	}
	
	/**
	 * Return an long value which is no bigger than the square root of the specified long, and
	 * can is evenly divisble.
	 */
	public static def sqrt(n:Long):Long {
		var rt:Long = Math.sqrt(n) as Long;
		for (; rt > 1L && n%rt != 0L; rt--);
		return rt;
	}
}
