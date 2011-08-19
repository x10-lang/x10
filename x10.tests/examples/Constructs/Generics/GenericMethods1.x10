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
 * @author bdlucas 8/2008
 */

public class GenericMethods1 extends GenericTest {

    def m[T](t:T) = t;

    public def run() = {

        check("m[int](1)", m[int](1), 1);
        check("m[String](\"1\")", m[String]("1"), "1");

        return result;
    }

    public static def main(var args: Array[String](1)): void = {
        new GenericMethods1().execute();
    }
}
