// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

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
    static type A[U] = int;

    public def run(): boolean = {

        type A = String;

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading17_MustFailCompile().execute();
    }
}
