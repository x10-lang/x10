// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: != constraints not supported

import harness.x10Test;

/**
 * @author bdlucas 8/2008
 */

public class GenericConstraint02_MustFailCompile extends GenericTest {

    class A[T,U]{!(T==U)} {}
    
    class X {};
    static type X1 = X;

    var a:A[X,X1];

    public def run(): boolean = {
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericConstraint02_MustFailCompile().execute();
    }
}
