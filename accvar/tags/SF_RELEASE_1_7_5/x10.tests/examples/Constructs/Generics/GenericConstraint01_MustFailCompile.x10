// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 8/2008
 */

public class GenericConstraint01_MustFailCompile extends GenericTest {

    class A[T,U]{T==U} {}

    class X {};
    class Y {};

    var a:A[X,Y];

    public def run(): boolean = {
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericConstraint01_MustFailCompile().execute();
    }
}
