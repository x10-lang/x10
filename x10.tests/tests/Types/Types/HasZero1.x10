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

import harness.x10Test;


/**
 * @author mtake 7/2012
 */
public class HasZero1 extends x10Test {
	
	static def f[T]():Boolean = T haszero;

    public def run():Boolean {
        chk(Int haszero);
        chk(f[Int]());
        
        return true;
    }

    public static def main(Rail[String]) {
        new HasZero1().execute();
    }

}
