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

public class NestedClass1[T] extends x10Test {

    static abstract class Base {}
    static class Sub[U] extends Base {}

    public def run(): Boolean = {
        return true;
    }

    public static def main(args: Rail[String]) {
        new NestedClass1[Int]().execute();
    }

}
