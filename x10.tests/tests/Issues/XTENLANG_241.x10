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
 * @author bdlucas 12/2008
 */
public class XTENLANG_241 extends x10Test {

    public operator - this: XTENLANG_241 = this;
    
    public def bar(p: XTENLANG_241) {
        val q = -p;
    }
    
    public def foo(p: Point) {
        val q = -p;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_241().execute();
    }
}
