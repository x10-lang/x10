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

public class JavaSerialization extends x10Test {
    def testFile() {
        val file = new java.io.File("/user");
        val orig = file.getParentFile();
        //Console.OUT.println(orig);
        at (here) {
            val copy = file.getParentFile();
            //Console.OUT.println(copy);
            chk(orig.equals(copy));
        }
    }

    def testHashSet() {
        val hashSet = new java.util.HashSet();
        val orig = hashSet.toString();
        //Console.OUT.println(orig);
        at (here) {
            val copy = hashSet.toString();
            //Console.OUT.println(copy);
            chk(orig.equals(copy));
        }
    }

    public def run(): Boolean = {
        testFile();
        testHashSet();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaSerialization().execute();
    }
}
