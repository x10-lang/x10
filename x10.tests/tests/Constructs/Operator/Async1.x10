/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.util.*;
import x10.compiler.Uncounted;

/**
 * Test operator redefinition. Example that lanches async in the scope of were the object of type Escape is created.
 * @author mandel
 */

class Async1 extends x10Test {

    class Escape {
	private var task: ()=>void = null;
	private var stop: Boolean = false;

	public def this() {
	    async {
		while (!stop) {
		    val t: () => void;
		    when (task != null || stop) {
			t = task;
			task = null;
		    }
		    if (t != null) {
			async { t(); }
		    }
		}
	    }
	}

	public def stop() {
	    atomic { stop = true; }
	}

	public operator async (body: () => void) {
	    when (task == null) {
		task = body;
	    }
	}
    }

    private var stop: Boolean = false;
    public def run() : boolean {
	val toplevel = new Escape();

	finish {
	    toplevel.async {
		when (stop){}
	    }
	}
	atomic { stop = true; }
	toplevel.stop();
	return true;
    }

    public static def main(Rail[String]) {
        new Async1().execute();
    }
}
