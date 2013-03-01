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
 * Test equality of UBytes.
 *
 * @author Salikh Zakirov 5/2011
 */
public class UByteOverride0 extends x10Test {
    static class A[T] {
	def f(x:T) : String {
	    return "T";
	}
    }

    static class A0 extends A[UByte] {
    }

    static class AByte extends A[Byte] {
	def f(x:UByte) : String { return "UByte"; }
    }

    static class AUByte extends A[UByte] {
	def f(x:Byte) : String { return "Byte"; }
    }

    static class B extends A[UByte] {
	def f(x:UByte) : String { return "UByte"; }
	def f(x:Byte) : String { return "Byte"; }
    }

    public def run() : Boolean {
	val a = new A[UByte]();
	val a0 = new A0();
	val abyte = new AByte();
	val aubyte = new AUByte();
	val b = new B();

	var r : Boolean = true;
	r = r && "T".equals(a.f(1uy));
	r = r && "T".equals(a0.f(1uy));
	r = r && "T".equals(abyte.f(1y));
	r = r && "UByte".equals(abyte.f(1uy));
	r = r && "T".equals(aubyte.f(1uy));
	r = r && "Byte".equals(aubyte.f(1y));
	r = r && "UByte".equals(b.f(1uy));
	r = r && "Byte".equals(b.f(1y));

	return r;
    }

    public static def main(Rail[String]) {
        new UByteOverride0().execute();
    }
}
