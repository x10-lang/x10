/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011.
 */

import harness.x10Test;
import x10.util.Ordered;
import x10.lang.Bitwise;
import x10.lang.Arithmetic;

/**
 * Test equality of UInts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntOps extends x10Test {
    public def run(): Boolean {
	var r : Boolean = true;
	if (!testUIntOps()) r = false;
	if (!testComparable()) r = false;
	if (!testArithmetic()) r = false;
	if (!testOrdered()) r = false;
	if (!testBitwise()) r = false;
	return r;
    }

    public def testUIntOps() : Boolean {
	var r : Boolean = true;
	val u1 = 1u;
	if (1u + 1u != 2u) r = false;
	if (u1 + 1u != 2u) r = false;
	if (u1 + u1 != 2u) r = false;
	if (u1 > 2u) r = false;
	if (2u < u1) r = false;
	if (u1 >= 2u) r = false;
	if (2u <= u1) r = false;
	if (2u - u1 != 1u) r = false;
	if (1u - 2u != 0xFFFFffffU) r = false;
	if (3u * 4u != 12u) r = false;
	if (4u / 3u != 1u) r = false;
	if (4u % 3u != 1u) r = false;
	return r;
    }

    public def testComparable() : Boolean {
	var r : Boolean = true;

	if (3u.compareTo(3u) != 0) 			{ p(" (3u.compareTo(3u) != 0) 		    "); r = false; }
	if (4u.compareTo(0xFFFFffffU) != -1)		{ p(" (4u.compareTo(0xFFFFffffU) != -1)	    "); r = false; }
	if (0xF0000001u.compareTo(0xF0000000u) != 1)    { p(" (0xF0000001u.compareTo(0xF0000000u) != 1 "); r = false; }

	val u3 = 3u as Comparable[UInt];
	val uf = 0xFFFFffffU as Comparable[UInt];
	val u4 = 4u as Comparable[UInt];
	if (u3.compareTo(3u) != 0) 			{ p(" (u3.compareTo(3u) != 0) 		    "); r = false; }
	if (u4.compareTo(0xFFFFffffU) != -1)		{ p(" (u4.compareTo(0xFFFFffffU) != -1)	    "); r = false; }
	if (uf.compareTo(0xF0000000u) != 1)    		{ p(" (uf.compareTo(0xF0000000u) != 1 "); r = false; }
	return r;
    }

    public def testArithmetic() : Boolean {

	var r : Boolean = true;

	if (0xFFFFffffU / 0x10u != 0xFFFffffU) 		{ p(" (0xFFFFffffU / 0x10u != 0xFFFffffU) "); r = false; }
	if (0xFFFFffffU / 0xFFFffffU != 0x10u)		{ p(" (0xFFFFffffU / 0xFFFffffU != 0x10u) "); r = false; }

	val uff = 0xFFFFffffU as Arithmetic[UInt];
	val uf0 = 0xFFFFfff0U as Arithmetic[UInt];
	val uf = 0xFFFffffU as Arithmetic[UInt];
	val u10 = 0x10000000U as Arithmetic[UInt];
	if (uff / 0x10u != uf)				{ p(" (uff / 0x10u != uf)       "); r = false; }
	if (uff / 0xFFFffffU != 0x10u)			{ p(" (uff / 0xFFFffffU != 0x10u) "); r = false; }

	if (0xFFFffffU * 0x10u != 0xFFFFfff0U)		{ p(" (0xFFFffffU * 0x10u != 0xFFFFfff0U)	 "); r = false; }
	if (uf * 0x10u != uf0)				{ p(" (uf * 0x10u != uf0)			 "); r = false; }
	if (0x0FFFffffU + 1u != 0x10000000U)		{ p(" (0x0FFFffffU + 1u != 0x10000000U)	 "); r = false; }
	if (uf + 1u != u10)				{ p(" (uf + 1u != u10)			 "); r = false; }
	if (0x10000000U - 1u != 0x0FFFffffU)		{ p(" (0x10000000U - 1u != 0x0FFFffffU)	 "); r = false; }
	if (u10 - 1u != uf)				{ p(" (u10 - 1u != uf)			 "); r = false; }

	return r;
    }

    public def testOrdered() : Boolean {
	var r : Boolean = true;
	if (0xFFFFffffU < 0x0FFFffffU)			{ p(" (0xFFFFffffU < 0x0FFFffffU)	 "); r = false; }
	if (0x0FFFffffU > 0xFFFFffffU)			{ p(" (0x0FFFffffU > 0xFFFFffffU)	 "); r = false; }
	if (0u >= 0x0FFFffffU)				{ p(" (0u >= 0x0FFFffffU)		 "); r = false; }
	if (0xFFFFffffU <= 0u)				{ p(" (0xFFFFffffU <= 0u)		 "); r = false; }

	val uff = 0xFFFFffffU as Ordered[UInt];
	val uf = 0x0FFFffffU as Ordered[UInt];
	if (uff < 0x0FFFffffu)		{ p(" (uff < 0x0FFFffffu)  "); r = false; }
	if (uf > 0xFFFFffffu)		{ p(" (uf > 0xFFFFffffu)  "); r = false; }
	if (0u >= 0x0FFFffffu)		{ p(" (0u >= 0x0FFFffffu)  "); r = false; }
	if (uff <= 0u)		{ p(" (uff <= 0u) "); r = false; }

	return r;
    }

    public def testBitwise() : Boolean {
	var r : Boolean = true;

	if ((~0x0FFFffffu) != 0xF0000000u)		{ p(" ((~0x0FFFffffu) != 0xF0000000u)		 "); r = false; }
	if ((0x0FFFffffu | 0xF0000000u) != 0xFFFFffffu) 	{ p(" (0x0FFFffffu | 0xF0000000u != 0xFFFFffffu) 	 "); r = false; }
	if ((0x0FFFffffu | 0x0000000Fu) != 0x0FFFffffu) 	{ p(" (0x0FFFffffu | 0x0000000Fu != 0x0FFFffffu) 	 "); r = false; }
	if ((0x0FFFffffu & 0xF0000000u) != 0u)		{ p(" (0x0FFFffffu & 0xF0000000u != 0u)		 "); r = false; }
	if ((0xFFFFffffu & 0xF0000000u) != 0xF0000000u)	{ p(" (0xFFFFffffu & 0xF0000000u != 0xF000000u)	 "); r = false; }
	if ((0x0FFFffffu ^ 0xF0000000u) != 0xFFFFffffu) 	{ p(" (0x0FFFffffu ^ 0xF0000000u != 0xFFFFffffu) 	 "); r = false; }
	if ((0xFFFFffffu ^ 0xF0000000u) != 0x0FFFffffu) 	{ p(" (0xFFFFffffu ^ 0xF0000000u != 0x0FFFffffu) 	 "); r = false; }
	if ((0xFFFFffffu >> 4) != 0x0FFFffffu)		{ p(" (0xFFFFffffu >> 4 != 0x0FFFffffu)		 "); r = false; }
	if ((0xFFFFffffu >> 1) != 0x7FFFffffu)		{ p(" (0xFFFFffffu >> 1 != 0x7FFFffffu)		 "); r = false; }
	if ((0xFFFFffffu >>> 4) != 0x0FFFffffu)		{ p(" (0xFFFFffffu >>> 4 != 0x0FFFffffu)		 "); r = false; }
	if ((0xFFFFffffu >>> 1) != 0x7FFFffffu)		{ p(" (0xFFFFffffu >>> 1 != 0x7FFFffffu)		 "); r = false; }
	if ((0xFFFFffffu << 4) != 0xFFFFfff0u)		{ p(" (0xFFFFffffu << 4 != 0xFFFFfff0u)		 "); r = false; }
	if ((0xFFFFffffu << 1) != 0xFFFFfffeu)		{ p(" (0xFFFFffffu << 1 != 0xFFFFfffeu)		 "); r = false; }

	val uff = 0xFFFFffffu as Bitwise[UInt];
	val u0f = 0x0FFFffffu as Bitwise[UInt];
	val u7f = 0x7FFFffffu as Bitwise[UInt];
	if ((~u0f) != 0xF0000000u)		{ p(" ((~u0f) != 0xF0000000u)		 "); r = false; }
	if ((u0f | 0xF0000000u) != uff) 	{ p(" (u0f | 0xF0000000u != uff) 	 "); r = false; }
	if ((u0f | 0x0000000Fu) != u0f) 	{ p(" (u0f | 0x0000000Fu != u0f) 	 "); r = false; }
	if ((u0f & 0xF0000000u) != 0u)		{ p(" (u0f & 0xF0000000u != 0u)		 "); r = false; }
	if ((uff & 0xF0000000u) != 0xF0000000u)	{ p(" (uff & 0xF0000000u != 0xF000000u)	 "); r = false; }
	if ((u0f ^ 0xF0000000u) != uff) 	{ p(" (u0f ^ 0xF0000000u != uff) 	 "); r = false; }
	if ((uff ^ 0xF0000000u) != u0f) 	{ p(" (uff ^ 0xF0000000u != u0f) 	 "); r = false; }
	if ((uff >> 4) != u0f)		{ p(" (uff >> 4 != u0f)		 "); r = false; }
	if ((uff >> 1) != u7f)		{ p(" (uff >> 1 != u7f)		 "); r = false; }
	if ((uff >>> 4) != u0f)		{ p(" (uff >>> 4 != u0f)		 "); r = false; }
	if ((uff >>> 1) != u7f)		{ p(" (uff >>> 1 != u7f)		 "); r = false; }
	if ((uff << 4) != 0xFFFFfff0u)		{ p(" (uff << 4 != 0xFFFFfff0u)		 "); r = false; }
	if ((uff << 1) != 0xFFFFfffeu)		{ p(" (uff << 1 != 0xFFFFfffeu)		 "); r = false; }

	return r;
    }

    public static def main(Rail[String]) {
        new UIntOps().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
