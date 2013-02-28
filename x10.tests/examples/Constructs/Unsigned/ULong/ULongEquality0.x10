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
 * Test equality of ULongs.
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongEquality0 extends x10Test {
    public def run(): boolean = {
	var r:Boolean = true;
        val u1 = 1ul;
	val u1any = u1 as Any;
	val u2 = 0xFFFFffffFFFFfffful;
	val u2any = u2 as Any;
	val v1any = (2ul-1ul) as Any;
	val v2any = (0ul-1ul) as Any;
        if (!(u1 == 1ul)) { p("! u1 == 1ul"); r = false; }
	if (u2 != 0xFFFFffffFFFFfffful) { p("u2 != 0xFFFFffffFFFFfffful"); r = false; }
	if (!(u1 == u1any)) { p("! u1 == u1 as Any"); r = false; }
	if (!(u1any == u1)) { p("! u1 as Any == u1"); r = false; }
	if (!(u2 == u2any)) { p("! u2 == u2 as Any"); r = false; }
	if (!(u2any == u2)) { p("! u2 as Any == u2"); r = false; }
	if (!(u1any == v1any)) { p("! u1 as Any == 2ul-1ul as Any"); r = false; }
	if (v2any != u2any) { p("0ul-1ul as Any != 0xFFFFffffFFFFfffful"); r = false; }
	if (u1any == u2any) { p("u1 as Any == u2 as Any"); r = false; }

	val i1 = 1l;
	val i1any = i1 as Any;
	// signed and unsigned values are disjoint in X10
	if (u1 == i1any) { p("u1 == i1 as Any"); r = false; }
	if (i1any == u1) { p("i1 as Any == u1"); r = false; }
	if (!(u1 != i1any)) { p("! u1 != i1 as Any"); r = false; }
	if (u1any == i1any) { p("u1 as Any == i1 as Any"); r = false; }
	if (i1any == u1any) { p("i1 as Any == u1 as Any"); r = false; }
	if (!(i1any != u1any)) { p("! i1 as Any != u1 as Any"); r = false; }

	if (0xFFFFffffFFFFfffful == (-1l) as Any) { p("(0xFFFFffffFFFFfffful == (-1l) as Any)"); r = false; }
	if (0xFFFFffffFFFFfffful as Any == (-1l) as Any) { p("(0xFFFFffffFFFFfffful as Any == (-1l) as Any)"); r = false; }
	if (0xFFFFffffFFFFfffful as Any == (-1l)) { p("(0xFFFFffffFFFFfffful as Any == (-1l))"); r = false; }
	if (-1l == 0xFFFFffffFFFFfffful as Any) { p("(-1l == 0xFFFFffffFFFFfffful as Any)"); r = false; }
	if ((-1l) as Any == 0xFFFFffffFFFFfffful as Any) { p("((-1l) as Any == 0xFFFFffffFFFFfffful as Any)"); r = false; }
	if ((-1l) as Any == 0xFFFFffffFFFFfffful) { p("((-1l) as Any == 0xFFFFffffFFFFfffful)"); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new ULongEquality0().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}

