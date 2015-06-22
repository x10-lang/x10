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

public class JavaSerialization4b extends x10Test {

    static class MyInt extends java.lang.Number {
        val i:Int;
        def this(i:Int) {
            this.i = i;
        }
        public def intValue():Int = i;
        public def longValue():Long = i as Long;
        public def floatValue():Float = i as Float;
        public def doubleValue():Double = i as Double;
    }
        
    static def test():Boolean {
        val e = new MyInt(99n);
        val j = e.intValue();
        Console.OUT.println(j);
        if (99n != j) return false;
        val ok = at (Place.places().next(here)) {
            val i = e.intValue();
            Console.OUT.println(i);
            return 99n == i;
        };
        return ok;
    }

    public def run(): Boolean {
        return test();
    }

    public static def main(args: Rail[String]) {
        new JavaSerialization4b().execute();
    }

}
