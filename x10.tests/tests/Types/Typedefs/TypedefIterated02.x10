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
 * Basic typdefs and type equivalence.
 *
 * Type definitions are applicative, not generative; that is, they define
 * aliases for types and do not introduce new types.
 *
 * @author bdlucas 9/2008
 */

public class TypedefIterated02 extends TypedefTest {

    public def run(): boolean = {
        type X[T,U] = T;
        type A[T] = X[T,String]; // = T
        type B = int;
        type C = A[B]; // = X[B,String] = B = int
        var c:C = 0n;
        type D = A[A[B]]; // = X[A[B],String] = A[B] = X[B,String] = B = int
        var d:D = 0n;
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefIterated02().execute();
    }
}
