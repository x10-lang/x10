// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class TypedefExample2 extends TypedefTest {

    public def run(): boolean = {
        
        type A = Int;
        type B = String;
        type C = String;
        a: A = 3;
        b: B = new C("Hi");
        c: C = b + ", Mom!";

        // XXX just syntax and type check for now

        return result;
    }



    public static def main(var args: Rail[String]): void = {
        new TypedefExample2().execute();
    }
}
