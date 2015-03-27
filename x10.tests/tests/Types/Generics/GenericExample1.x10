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

/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class GenericExample1 extends GenericTest {

        static class List[T] {
            var head: T;
            var tail: List[T];
            def this(h: T, t: List[T]) { head = h; tail = t; }
            def append(x: T) {
                if (this.tail == null)
                    this.tail = new List[T](x, null);
                else
                    this.tail.append(x);
            }
        }

    public def run() {

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new GenericExample1().execute();
    }
}
