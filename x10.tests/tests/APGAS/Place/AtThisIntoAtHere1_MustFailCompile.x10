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

//OPTIONS: -STATIC_CHECKS

import harness.x10Test;

/**
 * Changed for 2.1.
 * 
 * Test that if you have two fields with GlobalRef's pointing to the same location, you can do an at to
 * one field and deref the other provided that there is no intervening place shift.
 *
 * @author vj
 */
public class AtThisIntoAtHere1_MustFailCompile extends x10Test {
    class Test {
        private val x:GlobalRef[Test] = GlobalRef[Test](this);
        private val y:GlobalRef[Test]{self.home==x.home} = GlobalRef[Test](this); // ERR

        def n() {
            val p = Place(1);
            at (x) {
                at (p) {
                    // this is not ok because of the place shift.
                    y(); // ERR
                }
            }
        }
    }
    class Test1 {
        private val x = GlobalRef[Test1](this);
        private val y:GlobalRef[Test1]{self.home==x.home} = GlobalRef[Test1](this);

        def n() {
            val p = Place(1);
            at (x) {
                at (p) {
                    // this is not ok because of the place shift.
                    y(); // ERR
                }
            }
        }
    }
    class Test2 {
        private val x:GlobalRef[Test2]{self.home==here} = GlobalRef[Test2](this); // ERR: Cannot use "here" in this context
        private val y:GlobalRef[Test2]{self.home==here} = GlobalRef[Test2](this); // ERR: Cannot use "here" in this context

        def n() {
            val p = Place(1);
            at (x) {
                at (p) {
                    // this is not ok because of the place shift.
                    y(); // ERR
                }
            }
        }
    }
    class Test3 {
        private val x = GlobalRef[Test3](this);
        private val y = GlobalRef[Test3](this);

        def n() {
            val p = Place(1);
            at (x) {
                at (p) {
                    // this is not ok because of the place shift.
                    y(); // ERR
                }
            }
        }
    }
    class Test4 {
        private val x = GlobalRef(this);
        private val y = GlobalRef(this);

        def n() {
            val p = Place(1);
            at (x) {
                at (p) {
                    // this is not ok because of the place shift.
                    y(); // ERR
                }
            }
        }
    }

    public def run() = true;

    public static def main(Rail[String]) {
        new AtThisIntoAtHere1_MustFailCompile().execute();
    }
}
