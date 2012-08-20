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
import x10.interop.Java;
import x10.compiler.Native;

// MANAGED_X10_ONLY

public class JavaSerialization4 extends x10Test {

    static class MyException extends Exception {}
        
    static def test():void {
        val e = new MyException();
        at (here.next()) {
            e.toString();
        }
    }

    public def run(): Boolean = {
        test();
        return true;
    }

    public static def main(args: Array[String](1)) {
        new JavaSerialization4().execute();
    }

}
