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
public class ULongReturnType extends x10Test {
    static class A[T] {
	val value : T;

	def this(value:T) {
	    this.value = value;
	}

	def aaa():T {
	    return value;
	}
    }

    static class AULong extends A[ULong] {
	def this(value:ULong) {
	    super(value);
	}

	def bbb():ULong {
	    return aaa()+0xFFFFffffFFFFfffful;
	}
    }

    static class ALong extends A[Long] {
	def this(value:Long) {
	    super(value);
	}

	def bbb():Long {
	    return aaa()-1;
	}
    }

    public def run() : Boolean {
	if (new AULong(1ul).bbb() != 0ul) return false;
	if (new ALong(1).bbb() != 0l) return false;
	return true;
    }

    public static def main(Rail[String]) {
        new ULongReturnType().execute();
    }
}
