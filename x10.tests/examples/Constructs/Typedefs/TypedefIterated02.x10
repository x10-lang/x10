// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

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
        type A[T] = X[T,String];
        type B = int;
        type C = A[B];
        var c:C = 0;
        type D = A[A[B]];
        var d:D = 0;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefIterated02().execute();
    }
}
