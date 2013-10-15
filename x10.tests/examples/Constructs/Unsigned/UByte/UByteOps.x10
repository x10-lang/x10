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
 * Test equality of UBytes.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteOps extends x10Test {
    public def run(): Boolean {
	var r : Boolean = true;
	if (!testUByteOps()) r = false;
	if (!testComparable()) r = false;
	if (!testArithmetic()) r = false;
	if (!testOrdered()) r = false;
	if (!testBitwise()) r = false;
	return r;
    }

    public def testUByteOps() : Boolean {
	var r : Boolean = true;
	val u1 = 1uy;
	if (1uy + 1uy != 2uy) r = false;
	if (u1 + 1uy != 2uy) r = false;
	if (u1 + u1 != 2uy) r = false;
	if (u1 > 2uy) r = false;
	if (2uy < u1) r = false;
	if (u1 >= 2uy) r = false;
	if (2uy <= u1) r = false;
	if (2uy - u1 != 1uy) r = false;
	if (1uy - 2uy != 0xFFuy) r = false;
	if (3uy * 4uy != 12uy) r = false;
	if (4uy / 3uy != 1uy) r = false;
	if (4uy % 3uy != 1uy) r = false;
	return r;
    }

    public def testComparable() : Boolean {
	var r : Boolean = true;

	if (3uy.compareTo(3uy) != 0n) 			{ p(" (3uy.compareTo(3uy) != 0) 		    "); r = false; }
	if (4uy.compareTo(0xFFuy) != -1n)		{ p(" (4uy.compareTo(0xFFuy) != -1)	    "); r = false; }
	if (0xF1uy.compareTo(0xF0uy) != 1n)    { p(" (0xF1uy.compareTo(0xF0uy) != 1 "); r = false; }

	val u3 = 3uy as Comparable[UByte];
	val uf = 0xFFuy as Comparable[UByte];
	val u4 = 4uy as Comparable[UByte];
	if (u3.compareTo(3uy) != 0n) 			{ p(" (u3.compareTo(3uy) != 0) 		    "); r = false; }
	if (u4.compareTo(0xFFuy) != -1n)		{ p(" (u4.compareTo(0xFFuy) != -1)	    "); r = false; }
	if (uf.compareTo(0xF0uy) != 1n)    		{ p(" (uf.compareTo(0xF0uy) != 1 "); r = false; }
	return r;
    }

    public def testArithmetic() : Boolean {

	var r : Boolean = true;

	if (0xFFuy / 0x10uy != 0x0Fuy) 		{ p(" (0xFFuy / 0x10uy != 0x0Fuy) "); r = false; }
	if (0xFFuy / 0x0Fuy != 0x11uy)		{ p(" (0xFFuy / 0x0Fuy != 0x11uy) "); r = false; }

	val uff = 0xFFuy as Arithmetic[UByte];
	val uf0 = 0xF0uy as Arithmetic[UByte];
	val uf = 0x0Fuy as Arithmetic[UByte];
	val u10 = 0x10uy as Arithmetic[UByte];
	if (uff / 0x10uy != uf)				{ p(" (uff / 0x10uy != uf)       "); r = false; }
	if (uff / 0x0Fuy != 0x11uy)			{ p(" (uff / 0x0Fuy != 0x11uy) "); r = false; }

	if (0x0Fuy * 0x10uy != 0xF0uy)		{ p(" (0x0Fuy * 0x10uy != 0xF0uy)	 "); r = false; }
	if (uf * 0x10uy != uf0)				{ p(" (uf * 0x10uy != uf0)			 "); r = false; }
	if (0x0Fuy + 1uy != 0x10uy)		{ p(" (0x0Fuy + 1uy != 0x10uy)	 "); r = false; }
	if (uf + 1uy != u10)				{ p(" (uf + 1uy != u10)			 "); r = false; }
	if (0x10uy - 1uy != 0x0Fuy)		{ p(" (0x10uy - 1uy != 0x0Fuy)	 "); r = false; }
	if (u10 - 1uy != uf)				{ p(" (u10 - 1uy != uf)			 "); r = false; }

	return r;
    }

    public def testOrdered() : Boolean {
	var r : Boolean = true;
	if (0xFFuy < 0x0Fuy)			{ p(" (0xFFuy < 0x0Fuy)	 "); r = false; }
	if (0x0Fuy > 0xFFuy)			{ p(" (0x0Fuy > 0xFFuy)	 "); r = false; }
	if (0uy >= 0x0Fuy)				{ p(" (0uy >= 0x0Fuy)		 "); r = false; }
	if (0xFFuy <= 0uy)				{ p(" (0xFFuy <= 0uy)		 "); r = false; }

	val uff = 0xFFuy as Ordered[UByte];
	val uf = 0x0Fuy as Ordered[UByte];
	if (uff < 0x0Fuy)		{ p(" (uff < 0x0Fuy)  "); r = false; }
	if (uf > 0xFFuy)		{ p(" (uf > 0xFFuy)  "); r = false; }
	if (0uy >= 0x0Fuy)		{ p(" (0uy >= 0x0Fuy)  "); r = false; }
	if (uff <= 0uy)		{ p(" (uff <= 0uy) "); r = false; }

	return r;
    }

    public def testBitwise() : Boolean {
	var r : Boolean = true;

	if ((~0x0Fuy) != 0xF0uy)		{ p(" ((~0x0Fuy) != 0xF0uy)		 "); r = false; }
	if ((0x0Fuy | 0xF0uy) != 0xFFuy) 	{ p(" (0x0Fuy | 0xF0uy != 0xFFuy) 	 "); r = false; }
	if ((0x0Fuy | 0xFuy) != 0x0Fuy) 	{ p(" (0x0Fuy | 0xFuy != 0x0Fuy) 	 "); r = false; }
	if ((0x0Fuy & 0xF0uy) != 0uy)		{ p(" (0x0Fuy & 0xF0uy != 0uy)		 "); r = false; }
	if ((0xFFuy & 0xF0uy) != 0xF0uy)	{ p(" (0xFFuy & 0xF0uy != 0xF0uy)	 "); r = false; }
	if ((0x0Fuy ^ 0xF0uy) != 0xFFuy) 	{ p(" (0x0Fuy ^ 0xF0uy != 0xFFuy) 	 "); r = false; }
	if ((0xFFuy ^ 0xF0uy) != 0x0Fuy) 	{ p(" (0xFFuy ^ 0xF0uy != 0x0Fuy) 	 "); r = false; }
	if ((0xFFuy >> 4n) != 0x0Fuy)		{ p(" (0xFFuy >> 4 != 0x0Fuy)		 "); r = false; }
	if ((0xFFuy >> 1n) != 0x7Fuy)		{ p(" (0xFFuy >> 1 != 0x7Fuy)		 "); r = false; }
	if ((0xFFuy >>> 4n) != 0x0Fuy)		{ p(" (0xFFuy >>> 4 != 0x0Fuy)		 "); r = false; }
	if ((0xFFuy >>> 1n) != 0x7Fuy)		{ p(" (0xFFuy >>> 1 != 0x7Fuy)		 "); r = false; }
	if ((0xFFuy << 4n) != 0xF0uy)		{ p(" (0xFFuy << 4 != 0xF0uy)		 "); r = false; }
	if ((0xFFuy << 1n) != 0xFEuy)		{ p(" (0xFFuy << 1 != 0xFEuy)		 "); r = false; }

	val uff = 0xFFuy as Bitwise[UByte];
	val u0f = 0x0Fuy as Bitwise[UByte];
	val u7f = 0x7Fuy as Bitwise[UByte];
	if ((~u0f) != 0xF0uy)		{ p(" ((~u0f) != 0xF0uy)		 "); r = false; }
	if ((u0f | 0xF0uy) != uff) 	{ p(" (u0f | 0xF0uy != uff) 	 "); r = false; }
	if ((u0f | 0xFuy) != u0f) 	{ p(" (u0f | 0xFuy != u0f) 	 "); r = false; }
	if ((u0f & 0xF0uy) != 0uy)		{ p(" (u0f & 0xF0uy != 0uy)		 "); r = false; }
	if ((uff & 0xF0uy) != 0xF0uy)	{ p(" (uff & 0xF0uy != 0xF0uy)	 "); r = false; }
	if ((u0f ^ 0xF0uy) != uff) 	{ p(" (u0f ^ 0xF0uy != uff) 	 "); r = false; }
	if ((uff ^ 0xF0uy) != u0f) 	{ p(" (uff ^ 0xF0uy != u0f) 	 "); r = false; }
	if ((uff >> 4n) != u0f)		{ p(" (uff >> 4 != u0f)		 "); r = false; }
	if ((uff >> 1n) != u7f)		{ p(" (uff >> 1 != u7f)		 "); r = false; }
	if ((uff >>> 4n) != u0f)		{ p(" (uff >>> 4 != u0f)		 "); r = false; }
	if ((uff >>> 1n) != u7f)		{ p(" (uff >>> 1 != u7f)		 "); r = false; }
	if ((uff << 4n) != 0xF0uy)		{ p(" (uff << 4 != 0xF0uy)		 "); r = false; }
	if ((uff << 1n) != 0xFEuy)		{ p(" (uff << 1 != 0xFEuy)		 "); r = false; }

	return r;
    }

    public static def main(Rail[String]) {
        new UByteOps().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
