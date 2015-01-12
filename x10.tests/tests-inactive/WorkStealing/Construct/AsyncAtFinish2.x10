/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
//OPTIONS: -WORK_STEALING=true

import harness.x10Test;
/*
 *  Async to another place, and then create a finish and async back.
 *  No block stmt between contructs
 */
public class AsyncAtFinish2 extends x10Test{
	
	var value:int;
	
    static class T {
        private val root = GlobalRef[T](this);
        transient var val_:Any;
    }

    public def run(): boolean = {
        val Other: Place = Place.places().next(here);
        val t = new T();
        val troot = t.root;
        finish async at(Other) finish {
            val t1: T = new T();
            async at (troot) troot().val_ = t1;
        }
        val result = (t.val_ as T).root.home == Other;
        return result;
    }

	public static def main(Rail[String]) {
        new AsyncAtFinish2().execute();
	}
}
