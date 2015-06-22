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

/**
 * Test equality of UInts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntEquality0 extends x10Test {
    public def run(): boolean {
	var r:Boolean = true;
        val u1:UInt = 1un;
	val u1any:Any = u1 as Any;
	val u2:UInt = 0xFFFFffffun;
	val u2any:Any = u2 as Any;
	val v1any:Any = (2un-1un) as Any;
	val v2any:Any = (0un-1un) as Any;
        if (!(u1 == 1un)) { p("! u1 == 1u"); r = false; }
	if (u2 != 0xFFFFffffun) { p("u2 != 0xFFFFffffU"); r = false; }
	if (!(u1 == u1any)) { p("! u1 == u1 as Any"); r = false; }
	if (!(u1any == u1)) { p("! u1 as Any == u1"); r = false; }
	if (!(u2 == u2any)) { p("! u2 == u2 as Any"); r = false; }
	if (!(u2any == u2)) { p("! u2 as Any == u2"); r = false; }
	if (!(u1any == v1any)) { p("! u1 as Any == 2u-1u as Any"); r = false; }
	if (v2any != u2any) { p("0u-1u as Any != 0xFFFFffffU"); r = false; }
	if (u1any == u2any) { p("u1 as Any == u2 as Any"); r = false; }

	val i1:Int = 1n;
	val i1any:Any = i1 as Any;
	// signed and unsigned values are disjoint in X10
	if (u1 == i1any) { p("u1 == i1 as Any"); r = false; }
	if (i1any == u1) { p("i1 as Any == u1"); r = false; }
	if (!(u1 != i1any)) { p("! u1 != i1 as Any"); r = false; }
	if (u1any == i1any) { p("u1 as Any == i1 as Any"); r = false; }
	if (i1any == u1any) { p("i1 as Any == u1 as Any"); r = false; }
	if (!(i1any != u1any)) { p("! i1 as Any != u1 as Any"); r = false; }

	if (0xffffFFFFun == (-1n) as Any) { p("(0xffffFFFFu == (-1) as Any)"); r = false; }
	if (0xffffFFFFun as Any == (-1n) as Any) { p("(0xffffFFFFu as Any == (-1) as Any)"); r = false; }
	if (0xffffFFFFun as Any == (-1n)) { p("(0xffffFFFFu as Any == (-1))"); r = false; }
	if (-1n == 0xffffFFFFun as Any) { p("(-1 == 0xffffFFFFu as Any)"); r = false; }
	if ((-1n) as Any == 0xffffFFFFun as Any) { p("((-1) as Any == 0xffffFFFFu as Any)"); r = false; }
	if ((-1n) as Any == 0xffffFFFFun) { p("((-1) as Any == 0xffffFFFFun)"); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new UIntEquality0().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}

