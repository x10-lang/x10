// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Parametrized type definitions specify new type constructors; the
 * type parameters of a type definition must be bound to yield a type.
 */

public class ClosureTypeParameters4 extends ClosureTest {

    public def run(): boolean = {
        
        var f: ([T]() => void)[String];

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureTypeParameters4().execute();
    }
}
