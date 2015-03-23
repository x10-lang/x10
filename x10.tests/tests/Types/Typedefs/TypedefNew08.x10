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
 * An instance of a defined type with no type parameters and no value
 * parameters may be used to instantiate an instance of a type. The type
 * has the same constructors with the same signature as its defining
 * type; however, a constructor may not be invoked using a given defined
 * type name if the constructor return type is not a subtype of the
 * defined type.
 *
 * @author bdlucas 9/2008
 */

public class TypedefNew08 extends TypedefTest {
    static class X {}
    static class Y {}

    static class A[C] {
        def this() {};
        def this(i:long):A[C] {};
    }

    static type T[C] = A[C];
    static type TX = A[X];
    static type TY = A[Y];
    public def run(): boolean {
        // sanity check
        val a1 = new A[X]();
        val a2 = new A[X](0);

        // allowed
        val t1:A[X] = new TX();   // A[X] <: A[X]
        val t3:A[X] = new TX(0);  // A[X] <: A[X]
        val t2:A[Y] = new TY();   // A[Y] <: A[Y]

        return result;
    }

    public static def main(Rail[String]) {
        new TypedefNew08().execute();
    }
}
