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
public class UIntReturnType extends x10Test {
    static class A[T] {
	val value : T;

	def this(value:T) {
	    this.value = value;
	}

	def aaa():T {
	    return value;
	}
    }

    static class AUInt extends A[UInt] {
	def this(value:UInt) {
	    super(value);
	}

	def bbb():UInt {
	    return aaa()+0xffffffffun;
	}
    }

    static class AInt extends A[Int] {
	def this(value:Int) {
	    super(value);
	}

	def bbb():Int {
	    return aaa()-1n;
	}
    }

    public def run() : Boolean {
	if (new AUInt(1un).bbb() != 0un) return false;
	if (new AInt(1n).bbb() != 0n) return false;
	return true;
    }

    public static def main(Rail[String]) {
        new UIntReturnType().execute();
    }
}
