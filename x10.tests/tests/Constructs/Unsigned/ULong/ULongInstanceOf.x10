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
 * Test casting of Long to ULong
 *
 * @author Salikh Zakirov 5/2011
 */
public class ULongInstanceOf extends x10Test {
    public def run(): boolean = {
	var r : Boolean = true;
	val xx = 1ul;
	val i1 = 1;
	val l1 = 1l;
	val b1 = 1y;

	val yy : Any = xx;
	if (!(xx instanceof ULong)) { p("!Ulong instanceof ULong"); r = false; }
	if (!(yy instanceof ULong)) { p("!Any instanceof ULong"); r = false; }
	//if (xx instanceof Long) { p("ULong instanceof Long"); return false; } // fails complation
	//if (i1 instanceof ULong) { p("Long instanceof ULong"); return false; } // fails compilation

	return r;
    }

    public static def p(m:String) {
	Console.OUT.println(m);
    }

    public static def main(Rail[String]) {
        new ULongInstanceOf().execute();
    }
}
