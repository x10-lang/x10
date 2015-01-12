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

// SKIP_MANAGED_X10 :  XTENLANG-3086: Implementation limitation of Managed X10.

public class ExceptionDepType1 extends x10Test {

    static interface I {}
    static class C[T]{T <: I} {
        // Implementation limitation in managed X10 2.3. see XTENLANG-3086.
        def f() {T <: CheckedThrowable} throws T {}
    }
    static class E extends CheckedException implements I {}

    def test():Boolean {
        var ok:Boolean = true;
        
        try {
            new C[E]().f();
        } catch (e:E) {
        }

        return ok;
    }
    
    public def run(): Boolean {
        return true;
    }

    public static def main(args: Rail[String]) {
        new ExceptionDepType1().execute();
    }

}
