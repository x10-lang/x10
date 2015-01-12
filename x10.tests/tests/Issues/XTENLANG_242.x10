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
public class XTENLANG_242 extends x10Test {
    static class A {
        val name1 = typeName();
        def name2() = typeName();
    }
    
    static class B extends A {}

    public def run():boolean {
	chk(this.typeName().equals("XTENLANG_242"));
        return new B().name1.equals("XTENLANG_242.B") && new B().name2().equals("XTENLANG_242.B");
    }

    public static def main(Rail[String]) {
        new XTENLANG_242().execute();
    }
}
