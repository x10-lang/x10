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
 * Test equality of UShorts.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UShortEquality0 extends x10Test {
    public def run(): boolean {
	var r:Boolean = true;
        val u1 = 1us;
	val u1any = u1 as Any;
	val u2 = 0xFFFFus;
	val u2any = u2 as Any;
	val v1any = (2us-1us) as Any;
	val v2any = (0us-1us) as Any;
        if (!(u1 == 1us)) { p("! u1 == 1us"); r = false; }
	if (u2 != 0xFFFFus) { p("u2 != 0xFFFFus"); r = false; }
	if (!(u1 == u1any)) { p("! u1 == u1 as Any"); r = false; }
	if (!(u1any == u1)) { p("! u1 as Any == u1"); r = false; }
	if (!(u2 == u2any)) { p("! u2 == u2 as Any"); r = false; }
	if (!(u2any == u2)) { p("! u2 as Any == u2"); r = false; }
	if (!(u1any == v1any)) { p("! u1 as Any == 2us-1us as Any"); r = false; }
	if (v2any != u2any) { p("0us-1us as Any != 0xFFFFus"); r = false; }
	if (u1any == u2any) { p("u1 as Any == u2 as Any"); r = false; }

	val i1 = 1s;
	val i1any = i1 as Any;
	// signed and unsigned values are disjoint in X10
	if (u1 == i1any) { p("u1 == i1 as Any"); r = false; }
	if (i1any == u1) { p("i1 as Any == u1"); r = false; }
	if (!(u1 != i1any)) { p("! u1 != i1 as Any"); r = false; }
	if (u1any == i1any) { p("u1 as Any == i1 as Any"); r = false; }
	if (i1any == u1any) { p("i1 as Any == u1 as Any"); r = false; }
	if (!(i1any != u1any)) { p("! i1 as Any != u1 as Any"); r = false; }

	if (0xFFFFus == (-1s) as Any) { p("(0xFFFFus == (-1s) as Any)"); r = false; }
	if (0xFFFFus as Any == (-1s) as Any) { p("(0xFFFFus as Any == (-1s) as Any)"); r = false; }
	if (0xFFFFus as Any == (-1s)) { p("(0xFFFFus as Any == (-1s))"); r = false; }
	if (-1s == 0xFFFFus as Any) { p("(-1s == 0xFFFFus as Any)"); r = false; }
	if ((-1s) as Any == 0xFFFFus as Any) { p("((-1s) as Any == 0xFFFFus as Any)"); r = false; }
	if ((-1s) as Any == 0xFFFFus) { p("((-1s) as Any == 0xFFFFus)"); r = false; }
	return r;
    }

    public static def main(Rail[String]) {
        new UShortEquality0().execute();
    }

    public static def p(msg:Any) {
	Console.OUT.println(msg);
    }
}

