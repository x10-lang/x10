/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * @author Christian Grothoff
 */
public class StructEquality extends x10Test {
    public def run(): boolean = {
        val v1 = V(1);
        val v2 = V(1);
        return v1 == v2;
    }

    public static def main(args: Rail[String]): void = {
        new StructEquality().execute();
    }

    static struct V {
        val v: int;
        def this(i: int) {
            this.v = i;
        }
    }
}
