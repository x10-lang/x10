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

// MANAGED_X10_ONLY

public class JavaType2 extends x10Test {

    def test() {
        val l = new java.util.ArrayList();
        val s = l.typeName();
        chk("java.util.ArrayList".equals(s));
        val d:Any = new java.util.Date();
        l.add(d);
        l.add(d);
        val ll = at (Place.places().next(here)) {
            val d0 = l.get(0n);
            val d1 = l.get(1n);
            chk(d0 != null);
            chk(d0 == d1);
            return l;
        };
        chk(l.equals(ll));
    }

    public def run(): Boolean = {
        test();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaType2().execute();
    }
}
