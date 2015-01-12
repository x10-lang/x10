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
 * At other place. Cannot pass WS compile.
 */
public class AtOtherPlace extends x10Test{
    
	static class T {
		private val root = GlobalRef[T](this);
		var val_:Any;
	}
    
    public def run(): boolean = {
        val Other  = Place.places().next(here);
        val t = (new T()).root;
        at (Other) {
            val t1 = new T();
            at (t) t().val_ = t1;
        }
        val result = (t().val_ as T).root.home == Other;
        return result;
    }

    public static def main(Rail[String]) {
        new AtOtherPlace().execute();
    }
    
    
}
