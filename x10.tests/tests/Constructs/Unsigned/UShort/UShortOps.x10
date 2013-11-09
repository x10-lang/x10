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
 * Test equality of UShorts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortOps extends x10Test {
    public def run(): Boolean {
	var r : Boolean = true;
	if (!testUShortOps()) r = false;
	if (!testComparable()) r = false;
	if (!testArithmetic()) r = false;
	if (!testOrdered()) r = false;
	if (!testBitwise()) r = false;
	return r;
    }

    public def testUShortOps() : Boolean {
	var r : Boolean = true;
	val u1 = 1us;
	if (1us + 1us != 2us) r = false;
	if (u1 + 1us != 2us) r = false;
	if (u1 + u1 != 2us) r = false;
	if (u1 > 2us) r = false;
	if (2us < u1) r = false;
	if (u1 >= 2us) r = false;
	if (2us <= u1) r = false;
	if (2us - u1 != 1us) r = false;
	if (1us - 2us != 0xFFFFus) r = false;
	if (3us * 4us != 12us) r = false;
	if (4us / 3us != 1us) r = false;
	if (4us % 3us != 1us) r = false;
	return r;
    }

    public def testComparable() : Boolean {
	var r : Boolean = true;

	if (3us.compareTo(3us) != 0n) 			{ p(" (3us.compareTo(3us) != 0) 		    "); r = false; }
	if (4us.compareTo(0xFFFFus) != -1n)		{ p(" (4us.compareTo(0xFFFFus) != -1)	    "); r = false; }
	if (0xF001us.compareTo(0xF000us) != 1n)    { p(" (0xF001us.compareTo(0xF000us) != 1 "); r = false; }

	val u3 = 3us as Comparable[UShort];
	val uf = 0xFFFFus as Comparable[UShort];
	val u4 = 4us as Comparable[UShort];
	if (u3.compareTo(3us) != 0n) 			{ p(" (u3.compareTo(3us) != 0) 		    "); r = false; }
	if (u4.compareTo(0xFFFFus) != -1n)		{ p(" (u4.compareTo(0xFFFFus) != -1)	    "); r = false; }
	if (uf.compareTo(0xF000us) != 1n)    		{ p(" (uf.compareTo(0xF000us) != 1 "); r = false; }
	return r;
    }

    public def testArithmetic() : Boolean {

	var r : Boolean = true;

	if (0xFFFFus / 0x10us != 0x0FFFus) 		{ p(" (0xFFFFus / 0x10us != 0x0FFFus) "); r = false; }
	if (0xFFFFus / 0x0FFFus != 0x10us)		{ p(" (0xFFFFus / 0x0FFFus != 0x10us) "); r = false; }

	val uff = 0xFFFFus as Arithmetic[UShort];
	val uf0 = 0xFFF0us as Arithmetic[UShort];
	val uf = 0x0FFFus as Arithmetic[UShort];
	val u10 = 0x1000us as Arithmetic[UShort];
	if (uff / 0x10us != uf)				{ p(" (uff / 0x10us != uf)       "); r = false; }
	if (uff / 0x0FFFus != 0x10us)			{ p(" (uff / 0x0FFFus != 0x10us) "); r = false; }

	if (0x0FFFus * 0x10us != 0xFFF0us)		{ p(" (0x0FFFus * 0x10us != 0xFFF0us)	 "); r = false; }
	if (uf * 0x10us != uf0)				{ p(" (uf * 0x10us != uf0)			 "); r = false; }
	if (0x0FFFus + 1us != 0x1000us)		{ p(" (0x0FFFus + 1us != 0x1000us)	 "); r = false; }
	if (uf + 1us != u10)				{ p(" (uf + 1us != u10)			 "); r = false; }
	if (0x1000us - 1us != 0x0FFFus)		{ p(" (0x1000us - 1us != 0x0FFFus)	 "); r = false; }
	if (u10 - 1us != uf)				{ p(" (u10 - 1us != uf)			 "); r = false; }

	return r;
    }

    public def testOrdered() : Boolean {
	var r : Boolean = true;
	if (0xFFFFus < 0x0FFFus)			{ p(" (0xFFFFus < 0x0FFFus)	 "); r = false; }
	if (0x0FFFus > 0xFFFFus)			{ p(" (0x0FFFus > 0xFFFFus)	 "); r = false; }
	if (0us >= 0x0FFFus)				{ p(" (0us >= 0x0FFFus)		 "); r = false; }
	if (0xFFFFus <= 0us)				{ p(" (0xFFFFus <= 0us)		 "); r = false; }

	val uff = 0xFFFFus as Ordered[UShort];
	val uf = 0x0FFFus as Ordered[UShort];
	if (uff < 0x0FFFus)		{ p(" (uff < 0x0FFFus)  "); r = false; }
	if (uf > 0xFFFFus)		{ p(" (uf > 0xFFFFus)  "); r = false; }
	if (0us >= 0x0FFFus)		{ p(" (0us >= 0x0FFFus)  "); r = false; }
	if (uff <= 0us)		{ p(" (uff <= 0us) "); r = false; }

	return r;
    }

    public def testBitwise() : Boolean {
	var r : Boolean = true;

	if ((~0x0FFFus) != 0xF000us)		{ p(" ((~0x0FFFus) != 0xF000us)		 "); r = false; }
	if ((0x0FFFus | 0xF000us) != 0xFFFFus) 	{ p(" (0x0FFFus | 0xF000us != 0xFFFFus) 	 "); r = false; }
	if ((0x0FFFus | 0xFus) != 0x0FFFus) 	{ p(" (0x0FFFus | 0xFus != 0x0FFFus) 	 "); r = false; }
	if ((0x0FFFus & 0xF000us) != 0us)		{ p(" (0x0FFFus & 0xF000us != 0us)		 "); r = false; }
	if ((0xFFFFus & 0xF000us) != 0xF000us)	{ p(" (0xFFFFus & 0xF000us != 0xF000000us)	 "); r = false; }
	if ((0x0FFFus ^ 0xF000us) != 0xFFFFus) 	{ p(" (0x0FFFus ^ 0xF000us != 0xFFFFus) 	 "); r = false; }
	if ((0xFFFFus ^ 0xF000us) != 0x0FFFus) 	{ p(" (0xFFFFus ^ 0xF000us != 0x0FFFus) 	 "); r = false; }
	if ((0xFFFFus >> 4n) != 0x0FFFus)		{ p(" (0xFFFFus >> 4 != 0x0FFFus)		 "); r = false; }
	if ((0xFFFFus >> 1n) != 0x7FFFus)		{ p(" (0xFFFFus >> 1 != 0x7FFFus)		 "); r = false; }
	if ((0xFFFFus >>> 4n) != 0x0FFFus)		{ p(" (0xFFFFus >>> 4 != 0x0FFFus)		 "); r = false; }
	if ((0xFFFFus >>> 1n) != 0x7FFFus)		{ p(" (0xFFFFus >>> 1 != 0x7FFFus)		 "); r = false; }
	if ((0xFFFFus << 4n) != 0xFFF0us)		{ p(" (0xFFFFus << 4 != 0xFFF0us)		 "); r = false; }
	if ((0xFFFFus << 1n) != 0xFFFEus)		{ p(" (0xFFFFus << 1 != 0xFFFEus)		 "); r = false; }

	val uff = 0xFFFFus as Bitwise[UShort];
	val u0f = 0x0FFFus as Bitwise[UShort];
	val u7f = 0x7FFFus as Bitwise[UShort];
	if ((~u0f) != 0xF000us)		{ p(" ((~u0f) != 0xF000us)		 "); r = false; }
	if ((u0f | 0xF000us) != uff) 	{ p(" (u0f | 0xF000us != uff) 	 "); r = false; }
	if ((u0f | 0xFus) != u0f) 	{ p(" (u0f | 0xFus != u0f) 	 "); r = false; }
	if ((u0f & 0xF000us) != 0us)		{ p(" (u0f & 0xF000us != 0us)		 "); r = false; }
	if ((uff & 0xF000us) != 0xF000us)	{ p(" (uff & 0xF000us != 0xF000000us)	 "); r = false; }
	if ((u0f ^ 0xF000us) != uff) 	{ p(" (u0f ^ 0xF000us != uff) 	 "); r = false; }
	if ((uff ^ 0xF000us) != u0f) 	{ p(" (uff ^ 0xF000us != u0f) 	 "); r = false; }
	if ((uff >> 4n) != u0f)		{ p(" (uff >> 4 != u0f)		 "); r = false; }
	if ((uff >> 1n) != u7f)		{ p(" (uff >> 1 != u7f)		 "); r = false; }
	if ((uff >>> 4n) != u0f)		{ p(" (uff >>> 4 != u0f)		 "); r = false; }
	if ((uff >>> 1n) != u7f)		{ p(" (uff >>> 1 != u7f)		 "); r = false; }
	if ((uff << 4n) != 0xFFF0us)		{ p(" (uff << 4 != 0xFFF0us)		 "); r = false; }
	if ((uff << 1n) != 0xFFFEus)		{ p(" (uff << 1 != 0xFFFEus)		 "); r = false; }

	return r;
    }

    public static def main(Rail[String]) {
        new UShortOps().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}
