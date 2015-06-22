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

//LIMITATION:
//The current release does not implement the check that for every constructor
// with a defined return type, every path to the exit node contains
// an invocation of property that is strong enough to entail the return type.

import harness.x10Test;

/**
 * Test that the compiler detects a situation in which one branch of a conditional has
 * a property clause but not another.
 *
 * @author vj
 */
public class PropertyNotInvoked2_MustFailCompile extends x10Test {

    static class Tester(i: int(2n)) {
        public def this(arg:int(2n)):Tester{self.i==arg} { // ERR: property(...) might not have been called
            if (arg == 2n) {
                property(arg);
            } else {
                i=2n; // ERR: Must use property(...) to assign to a property.
            }
        }
    }

    public def run(): boolean {
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new PropertyNotInvoked2_MustFailCompile().execute();
    }
}
