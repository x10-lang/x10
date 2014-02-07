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
 * Literal test, for java literals
 */
public class LiteralTest extends x10Test {
	var i1: int = -2147483648n;
	var i2: int = 020000000000n;
	var i3: int = 0x80000000n;
	var i4: int = 2147483647n;
	var i5: int = 0x7fffffffn;
	var i6: int = 017777777777n;
	var i7: int = 037777777777L as int;
	var i8: int = 0xffffffffL as int;
	var i9: int = -1n;
	var l1: long = -9223372036854775808L;
	var l2: long = 0x8000000000000000l;
	var l3: long = 01000000000000000000000l;
	var l4: long = 9223372036854775807L;
	var l5: long = 0x7fffffffffffffffL;
	var l6: long = 0777777777777777777777L;
	var l7: long = -1L;
	var l8: long = 01777777777777777777777L;
	var l9: long = 0xffffffffffffffffL;
	var foo: String = "a\r\n";

	// necessary to run this in the TestCompiler harness.
	public def this(): LiteralTest = { }

	public def run(): boolean = {
		chk(i1 == i2 && i2 == i3);
		chk(i1 == -i1);
		chk(i4 == i5 && i5 == i6);
		chk(i4+1n == i1 && i6 == i1-1n);
		chk(i7 == i8 && i8 == i9);
		chk(i9+1n == (i1-i3));
		chk((i9 << 31) == i1);
		chk(l1 == l2 &&l2 == l3 && l1 == -l1);
		chk(l1 == ((i1 as long)<<32));
		chk(l4 == l5 && l5 == l6);
		chk(l4+1 == l1 && l4 == l1-1);
		chk(l7 == l8 && l8 == l9 && l9 == i9 as Long);
		chk(l9+1 == (l1-l3));
		chk((l9 << 63) == l1);
		chk(foo.charAt(0n) == 'a');
		chk(foo.charAt(1n) == '\r');
		chk(foo.charAt(2n) == '\n');
		chk(foo.equals("a"+"\r"+"\n"));
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new LiteralTest().execute();
	}
}
