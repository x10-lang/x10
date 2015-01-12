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
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class GenericExample2 extends GenericTest {

        static class Cell[X] {
            var x: X;
            def this(x: X) { this.x = x; }
            def get(): X = x;
            def set(x: X) = { this.x = x; }
        }
        
        static class Get[X] {
            val x: X;
            def this(x: X) { this.x = x; }
            def get(): X = x;
        }

        static interface Set[X] {
            // var x: X; // var fields are invariant
            def set(x: X):void;
        }

    public def run() = {

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericExample2().execute();
    }
}
