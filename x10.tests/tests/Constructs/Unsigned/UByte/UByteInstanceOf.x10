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
 * Test casting of Byte to UByte
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteInstanceOf extends x10Test {
    public def run(): boolean = {
	var r : Boolean = true;
	val xx = 1uy;
	val i1 = 1;
	val l1 = 1y;
	val b1 = 1y;

	val yy : Any = xx;
	if (!(xx instanceof UByte)) { p("!Ubyte instanceof UByte"); r = false; }
	if (!(yy instanceof UByte)) { p("!Any instanceof UByte"); r = false; }
	//if (xx instanceof Byte) { p("UByte instanceof Byte"); return false; } // fails complation
	//if (i1 instanceof UByte) { p("Byte instanceof UByte"); return false; } // fails compilation

	return r;
    }

    public static def p(m:String) {
	Console.OUT.println(m);
    }

    public static def main(Rail[String]) {
        new UByteInstanceOf().execute();
    }
}
