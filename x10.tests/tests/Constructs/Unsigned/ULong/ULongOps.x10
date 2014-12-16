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
 * Test equality of ULongs.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongOps extends x10Test {
    public def run(): Boolean {
	var r : Boolean = true;
	if (!testULongOps()) r = false;
	if (!testComparable()) r = false;
	if (!testArithmetic()) r = false;
	if (!testOrdered()) r = false;
	if (!testBitwise()) r = false;
	return r;
    }

    public def testULongOps() : Boolean {
	var r : Boolean = true;
	val u1 = 1ul;
	if (1ul + 1ul != 2ul) r = false;
	if (u1 + 1ul != 2ul) r = false;
	if (u1 + u1 != 2ul) r = false;
	if (u1 > 2ul) r = false;
	if (2ul < u1) r = false;
	if (u1 >= 2ul) r = false;
	if (2ul <= u1) r = false;
	if (2ul - u1 != 1ul) r = false;
	if (1ul - 2ul != 0xFFFFffffFFFFfffful) r = false;
	if (3ul * 4ul != 12ul) r = false;
	if (4ul / 3ul != 1ul) r = false;
	if (4ul % 3ul != 1ul) r = false;
	return r;
    }

    public def testComparable() : Boolean {
	var r : Boolean = true;

	if (3ul.compareTo(3ul) != 0n) 			{ p(" (3ul.compareTo(3ul) != 0) 		    "); r = false; }
	if (4ul.compareTo(0xFFFFffffFFFFfffful) != -1n)		{ p(" (4ul.compareTo(0xFFFFffffFFFFfffful) != -1n)	    "); r = false; }
	if (0xF000000000000001ul.compareTo(0xF000000000000000ul) != 1n)    { p(" (0xF000000000000001ul.compareTo(0xF000000000000000ul) != 1 "); r = false; }

	val u3 = 3ul as Comparable[ULong];
	val uf = 0xFFFFffffFFFFfffful as Comparable[ULong];
	val u4 = 4ul as Comparable[ULong];
	if (u3.compareTo(3ul) != 0n) 			{ p(" (u3.compareTo(3ul) != 0) 		    "); r = false; }
	if (u4.compareTo(0xFFFFffffFFFFfffful) != -1n)		{ p(" (u4.compareTo(0xFFFFffffFFFFfffful) != -1)	    "); r = false; }
	if (uf.compareTo(0xF000000000000000ul) != 1n)    		{ p(" (uf.compareTo(0xF000000000000000ul) != 1 "); r = false; }
	return r;
    }

    public def testArithmetic() : Boolean {

	var r : Boolean = true;

	if (0xFFFFffffFFFFfffful / 0x10ul != 0x0FFFffffFFFFfffful) 		{ p(" (0xFFFFffffFFFFfffful / 0x10ul != 0x0FFFffffFFFFfffful) "); r = false; }
	if (0xFFFFffffFFFFfffful / 0x0FFFffffFFFFfffful != 0x10ul)		{ p(" (0xFFFFffffFFFFfffful / 0x0FFFffffFFFFfffful != 0x10ul) "); r = false; }

	val uff = 0xFFFFffffFFFFfffful as Arithmetic[ULong];
	val uf0 = 0xFFFFffffFFFFfff0ul as Arithmetic[ULong];
	val uf = 0x0FFFffffFFFFfffful as Arithmetic[ULong];
	val u10 = 0x1000000000000000ul as Arithmetic[ULong];
	if (uff / 0x10ul != uf)				{ p(" (uff / 0x10ul != uf)       "); r = false; }
	if (uff / 0x0FFFffffFFFFfffful != 0x10ul)			{ p(" (uff / 0x0FFFffffFFFFfffful != 0x10ul) "); r = false; }

	if (0x0FFFffffFFFFfffful * 0x10ul != 0xFFFFffffFFFFfff0ul)		{ p(" (0x0FFFffffFFFFfffful * 0x10ul != 0xFFFFffffFFFFfff0ul)	 "); r = false; }
	if (uf * 0x10ul != uf0)				{ p(" (uf * 0x10ul != uf0)			 "); r = false; }
	if (0x0FFFffffFFFFfffful + 1ul != 0x1000000000000000ul)		{ p(" (0x0FFFffffFFFFfffful + 1ul != 0x1000000000000000ul)	 "); r = false; }
	if (uf + 1ul != u10)				{ p(" (uf + 1ul != u10)			 "); r = false; }
	if (0x1000000000000000ul - 1ul != 0x0FFFffffFFFFfffful)		{ p(" (0x1000000000000000ul - 1ul != 0x0FFFffffFFFFfffful)	 "); r = false; }
	if (u10 - 1ul != uf)				{ p(" (u10 - 1ul != uf)			 "); r = false; }

	return r;
    }

    public def testOrdered() : Boolean {
	var r : Boolean = true;
	if (0xFFFFffffFFFFfffful < 0x0FFFffffFFFFfffful)			{ p(" (0xFFFFffffFFFFfffful < 0x0FFFffffFFFFfffful)	 "); r = false; }
	if (0x0FFFffffFFFFfffful > 0xFFFFffffFFFFfffful)			{ p(" (0x0FFFffffFFFFfffful > 0xFFFFffffFFFFfffful)	 "); r = false; }
	if (0ul >= 0x0FFFffffFFFFfffful)				{ p(" (0ul >= 0x0FFFffffFFFFfffful)		 "); r = false; }
	if (0xFFFFffffFFFFfffful <= 0ul)				{ p(" (0xFFFFffffFFFFfffful <= 0ul)		 "); r = false; }

	val uff = 0xFFFFffffFFFFfffful as Ordered[ULong];
	val uf = 0x0FFFffffFFFFfffful as Ordered[ULong];
	if (uff < 0x0FFFffffFFFFfffful)		{ p(" (uff < 0x0FFFffffFFFFfffful)  "); r = false; }
	if (uf > 0xFFFFffffFFFFfffful)		{ p(" (uf > 0xFFFFffffFFFFfffful)  "); r = false; }
	if (0ul >= 0x0FFFffffFFFFfffful)		{ p(" (0ul >= 0x0FFFffffFFFFfffful)  "); r = false; }
	if (uff <= 0ul)		{ p(" (uff <= 0ul) "); r = false; }

	return r;
    }

    public def testBitwise() : Boolean {
	var r : Boolean = true;

	if ((~0x0FFFffffFFFFfffful) != 0xF000000000000000ul)		{ p(" ((~0x0FFFffffFFFFfffful) != 0xF000000000000000ul)		 "); r = false; }
	if ((0x0FFFffffFFFFfffful | 0xF000000000000000ul) != 0xFFFFffffFFFFfffful) 	{ p(" (0x0FFFffffFFFFfffful | 0xF000000000000000ul != 0xFFFFffffFFFFfffful) 	 "); r = false; }
	if ((0x0FFFffffFFFFfffful | 0xFul) != 0x0FFFffffFFFFfffful) 	{ p(" (0x0FFFffffFFFFfffful | 0xFul != 0x0FFFffffFFFFfffful) 	 "); r = false; }
	if ((0x0FFFffffFFFFfffful & 0xF000000000000000ul) != 0ul)		{ p(" (0x0FFFffffFFFFfffful & 0xF000000000000000ul != 0ul)		 "); r = false; }
	if ((0xFFFFffffFFFFfffful & 0xF000000000000000ul) != 0xF000000000000000ul)	{ p(" (0xFFFFffffFFFFfffful & 0xF000000000000000ul != 0xF000000ul)	 "); r = false; }
	if ((0x0FFFffffFFFFfffful ^ 0xF000000000000000ul) != 0xFFFFffffFFFFfffful) 	{ p(" (0x0FFFffffFFFFfffful ^ 0xF000000000000000ul != 0xFFFFffffFFFFfffful) 	 "); r = false; }
	if ((0xFFFFffffFFFFfffful ^ 0xF000000000000000ul) != 0x0FFFffffFFFFfffful) 	{ p(" (0xFFFFffffFFFFfffful ^ 0xF000000000000000ul != 0x0FFFffffFFFFfffful) 	 "); r = false; }
	if ((0xFFFFffffFFFFfffful >> 4n) != 0x0FFFffffFFFFfffful)		{ p(" (0xFFFFffffFFFFfffful >> 4 != 0x0FFFffffFFFFfffful)		 "); r = false; }
	if ((0xFFFFffffFFFFfffful >> 1n) != 0x7FFFffffFFFFfffful)		{ p(" (0xFFFFffffFFFFfffful >> 1 != 0x7FFFffffFFFFfffful)		 "); r = false; }
	if ((0xFFFFffffFFFFfffful >>> 4n) != 0x0FFFffffFFFFfffful)		{ p(" (0xFFFFffffFFFFfffful >>> 4 != 0x0FFFffffFFFFfffful)		 "); r = false; }
	if ((0xFFFFffffFFFFfffful >>> 1n) != 0x7FFFffffFFFFfffful)		{ p(" (0xFFFFffffFFFFfffful >>> 1 != 0x7FFFffffFFFFfffful)		 "); r = false; }
	if ((0xFFFFffffFFFFfffful << 4n) != 0xFFFFffffFFFFfff0ul)		{ p(" (0xFFFFffffFFFFfffful << 4 != 0xFFFFffffFFFFfff0ul)		 "); r = false; }
	if ((0xFFFFffffFFFFfffful << 1n) != 0xFFFFffffFFFFfffeul)		{ p(" (0xFFFFffffFFFFfffful << 1 != 0xFFFFffffFFFFfffeul)		 "); r = false; }

	val uff = 0xFFFFffffFFFFfffful as Bitwise[ULong];
	val u0f = 0x0FFFffffFFFFfffful as Bitwise[ULong];
	val u7f = 0x7FFFffffFFFFfffful as Bitwise[ULong];
	if ((~u0f) != 0xF000000000000000ul)		{ p(" ((~u0f) != 0xF000000000000000ul)		 "); r = false; }
	if ((u0f | 0xF000000000000000ul) != uff) 	{ p(" (u0f | 0xF000000000000000ul != uff) 	 "); r = false; }
	if ((u0f | 0xFul) != u0f) 	{ p(" (u0f | 0xFul != u0f) 	 "); r = false; }
	if ((u0f & 0xF000000000000000ul) != 0ul)		{ p(" (u0f & 0xF000000000000000ul != 0ul)		 "); r = false; }
	if ((uff & 0xF000000000000000ul) != 0xF000000000000000ul)	{ p(" (uff & 0xF000000000000000ul != 0xF000000ul)	 "); r = false; }
	if ((u0f ^ 0xF000000000000000ul) != uff) 	{ p(" (u0f ^ 0xF000000000000000ul != uff) 	 "); r = false; }
	if ((uff ^ 0xF000000000000000ul) != u0f) 	{ p(" (uff ^ 0xF000000000000000ul != u0f) 	 "); r = false; }
	if ((uff >> 4n) != u0f)		{ p(" (uff >> 4 != u0f)		 "); r = false; }
	if ((uff >> 1n) != u7f)		{ p(" (uff >> 1 != u7f)		 "); r = false; }
	if ((uff >>> 4n) != u0f)		{ p(" (uff >>> 4 != u0f)		 "); r = false; }
	if ((uff >>> 1n) != u7f)		{ p(" (uff >>> 1 != u7f)		 "); r = false; }
	if ((uff << 4n) != 0xFFFFffffFFFFfff0ul)		{ p(" (uff << 4 != 0xFFFFffffFFFFfff0ul)		 "); r = false; }
	if ((uff << 1n) != 0xFFFFffffFFFFfffeul)		{ p(" (uff << 1 != 0xFFFFffffFFFFfffeul)		 "); r = false; }

	return r;
    }

    public static def main(Rail[String]) {
        new ULongOps().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
