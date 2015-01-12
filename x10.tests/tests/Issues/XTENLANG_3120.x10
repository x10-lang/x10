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

// MANAGED_X10_ONLY

import x10.io.Console;
import harness.x10Test;

public class XTENLANG_3120 extends x10Test {
    public static class Foo extends java.util.Date { }
    public static class Bar extends java.util.ArrayList { }
    public def run () {
        new Foo();
        new Bar();
        return true;
    }
    public static def main (args : Rail[String]) {
        (new XTENLANG_3120()).execute();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab

