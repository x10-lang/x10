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
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class TypedefExample2 extends TypedefTest {

    public def run(): boolean {
        
        type A = Int;
        type B = String;
        type C = String;
        a: A = 3n;
        b: B = new C("Hi");
        c: C = b + ", Mom!";

        // XXX just syntax and type check for now

        return result;
    }



    public static def main(var args: Rail[String]): void {
        new TypedefExample2().execute();
    }
}
