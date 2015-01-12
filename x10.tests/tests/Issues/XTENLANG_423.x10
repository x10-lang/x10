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
 * @author igor 7/2009
 */

public class XTENLANG_423 extends x10Test {

    public static class X {
        public static a = foo();
        public static def foo() = 3;
    }

    public static class Y extends X {
        public static b = bar();
        public static def bar() = X.a;
    }

    public static class Z[T] {
        public static c = baz();
        public static def baz() = 5;
    }

    public static class W[U] extends Z[W[U]] {
        public static d = boo();
        public static def boo() = Z.c;
    }

    public def run() : boolean {
        return Y.b == 3 && W.d == 5;
    }

    public static def main(Rail[String]) {
        new XTENLANG_423().execute();
    }
}
