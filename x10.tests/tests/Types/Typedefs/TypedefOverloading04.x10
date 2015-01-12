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
 * It is legal for a package, class, or interface to contain a type
 * definition with no type or value parameters and also a member class
 * or interface with the same name but with a different number of type
 * parameters.
 *
 * @author bdlucas 9/2008
 */

public class TypedefOverloading04 extends TypedefTest {

    class A[T] {}
    static type A = int;

    public def run(): boolean = {
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading04().execute();
    }
}
