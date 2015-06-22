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
 * It is legal for a package, class, or interface to contain more than
 * one type definition with the same name as long as the definitions
 * have different parameters according to the method overloading
 * rules.
 *
 * @author bdlucas 9/2008
 */

public class TypedefOverloading17_MustFailCompile extends TypedefTest {

    static type A[T] = int;
    static type A[U] = int; // ERR

    public def run(): boolean {

        type A = String;

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new TypedefOverloading17_MustFailCompile().execute();
    }
}
