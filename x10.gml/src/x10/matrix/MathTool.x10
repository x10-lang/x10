/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix;


/**
 *  Provides some static math methods.
 */

public class MathTool {
   
	public static delta:Double = 0.00000001;
    //public static  def max(a:Int, b:Int)   = (a <= b) ? b : a;
    //public static  def max(a:Float, b:Float)   = (a <= b) ? b : a;
    //public static  def max(a:Double, b:Double) = (a <= b) ? b : a;
	//
	//public static  def abs(a:Int)              = (a < 0)?-a:a;
    //public static  def abs(a:Float)            = (a <= 0.0F) ? -a : a;
    //public static  def abs(a:Double)           = (a <= 0.0D) ? -a : a;
	//
    //public static  def min(a:Double, b:Double) = (a < b) ? a : b;
	//public static  def min(a:Int, b:Int)       = (a < b) ? a : b;	
	//

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
	 * Return Euclidean distance between two scalar
	 */
	public static def euclideanDistance(a:Array[Double](1), 
									   b:Array[Double](1), len:Int):Double {
		var d:Double = 0.0;
		for (var i:Int=0; i<len; i++)
			d += (a(i)-b(i))*(a(i)-b(i));
		return Math.sqrt(d);
	}
	
	/**
	 * Return an integer value which is no bigger than the square root of a specified integer, which
	 * is evenly divisble.
	 */
	public static def sqrt(n:Int):Int {
		var rt:Int = Math.sqrt(n) as Int;
		for (; rt > 1 && n%rt != 0; rt--);
		return rt;
	}
}
