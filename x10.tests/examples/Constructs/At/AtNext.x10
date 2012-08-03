/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Testing the ability to assign to the field of an Any
 * at place here a reference to an Any at place here.next().
 *
 * @author vj
 */
public class AtNext extends x10Test {

	public def run(): boolean = {
		val Other  = here.next();
		val t = (new T()).root;
		at (Other) {
			val t1 = new T();
			at (t) t().val_ = t1;
		}
		return (t().val_ as T).root.home == Other;
	}

	public static def main(Array[String](1)) {
		new AtNext().execute();
	}

	static class T {
		private val root = GlobalRef[T](this);
		var val_:Any;
	}
}
