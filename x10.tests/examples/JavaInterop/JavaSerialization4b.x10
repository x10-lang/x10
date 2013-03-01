/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
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
        val e = new MyInt(99);
        val j = e.intValue();
        Console.OUT.println(j);
        if (99 != j) return false;
        val ok = at (here.next()) {
            val i = e.intValue();
            Console.OUT.println(i);
            return 99 == i;
        };
        return ok;
    }

    public def run(): Boolean = {
        return test();
    }

    public static def main(args: Rail[String]) {
        new JavaSerialization4b().execute();
    }

}
