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

// MANAGED_X10_ONLY

public class JavaType1 extends x10Test {

    def test() {
        val d:Any = new java.util.Date();
        val s = d.typeName();
        chk("java.util.Date".equals(s));
        val dd = at (Place.places().next(here)) {
            val ss = d.typeName();
            chk(s.equals(ss));
            return d;
        };
        chk(d.equals(dd));
    }

    public def run(): Boolean = {
        test();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaType1().execute();
    }
}
