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
public class UShortReturnType extends x10Test {
    static class A[T] {
	val value : T;

	def this(value:T) {
	    this.value = value;
	}

	def aaa():T {
	    return value;
	}
    }

    static class AUShort extends A[UShort] {
	def this(value:UShort) {
	    super(value);
	}

	def bbb():UShort {
	    return aaa()+0xFFFFus;
	}
    }

    static class AShort extends A[Short] {
	def this(value:Short) {
	    super(value);
	}

	def bbb():Short {
	    return aaa()-1s;
	}
    }

    public def run() : Boolean {
	if (new AUShort(1us).bbb() != 0us) return false;
	if (new AShort(1s).bbb() != 0s) return false;
	return true;
    }

    public static def main(Rail[String]) {
        new UShortReturnType().execute();
    }
}
