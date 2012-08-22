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
        public def intValue():Int = 0;
        public def longValue():Long = 0L;
        public def floatValue():Float = 0.0F;
        public def doubleValue():Double = 0.0;
    }
        
    static def test():void {
        val e = new MyInt();
        at (here.next()) {
        	e.toString();
        }
    }

    public def run(): Boolean = {
        test();
        return true;
    }

    public static def main(args: Array[String](1)) {
        new JavaSerialization4b().execute();
    }

}
