/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

import harness.x10Test;
import x10.compiler.tests.*; // err markers
import x10.util.*;


/**
 * 
 * @author lmandel
 */

public class TrailingClosure extends x10Test {


    public static def sync(body:()=>void) {
	body();
    }

    public static def loop(n: Long, body:()=>void) {
	for (var i: Long = 0; i < n; i++) {
	    body();
	}
    }

    public def run(): boolean {
	val x = new Cell[Long](0);
	sync {
	    x() = x() + 1;
	}
	assert x() == 1;
	loop(3) {
	    x() = x() + 1;
	}
	assert x() == 4;
	return true;
    }

    public static def main(var args: Rail[String]): void {
        new TrailingClosure().execute();
    }
}
