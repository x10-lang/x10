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
public class UByteReturnType extends x10Test {
    static class A[T] {
	val value : T;

	def this(value:T) {
	    this.value = value;
	}

	def aaa():T {
	    return value;
	}
    }

    static class AUByte extends A[UByte] {
	def this(value:UByte) {
	    super(value);
	}

	def bbb():UByte {
	    return aaa()+0xFFuy;
	}
    }

    static class AByte extends A[Byte] {
	def this(value:Byte) {
	    super(value);
	}

	def bbb():Byte {
	    return aaa()-1y;
	}
    }

    public def run() : Boolean {
	if (new AUByte(1uy).bbb() != 0uy) return false;
	if (new AByte(1y).bbb() != 0y) return false;
	return true;
    }

    public static def main(Rail[String]) {
        new UByteReturnType().execute();
    }
}
