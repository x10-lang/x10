/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Test casting of Int to UInt
 *
 * @author Salikh Zakirov 5/2011
 */
public class UIntInstanceOf extends x10Test {
    public def run(): boolean {
	val u1 = 1un;
	val i1 = 1n;
	val l1 = 1l;
	val b1 = 1y;

	val uu1 : Any = u1;
	if (!(u1 instanceof UInt)) { p("!Uint instanceof UInt"); return false; }
	if (!(uu1 instanceof UInt)) { p("!Any instanceof UInt"); return false; }
	//if (u1 instanceof Int) { p("UInt instanceof Int"); return false; } // fails complation
	//if (i1 instanceof UInt) { p("Int instanceof UInt"); return false; } // fails compilation

	return true;
    }

    public static def p(m:String) {
	Console.OUT.println(m);
    }

    public static def main(Rail[String]) {
        new UIntInstanceOf().execute();
    }
}
