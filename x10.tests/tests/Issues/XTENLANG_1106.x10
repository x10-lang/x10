/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * @author makinoy 4/2010
 */

class XTENLANG_1106 extends x10Test {

    public static interface I[T] { }
    public static struct S implements I[S] { }
    
    public def run(): boolean {
    	var x:S;
        return true;
    }
    
    public static def main(Rail[String]) {
		new XTENLANG_1106().execute();
    }
}
