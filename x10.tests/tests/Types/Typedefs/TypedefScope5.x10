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
 * Type definitions may appear as class members or in the body of a
 * method, constructor, or initializer.
 *
 * @author bdlucas 8/2008
 */

public class TypedefScope5 extends TypedefTest {

    public def run(): boolean = {
        
        val foo = () => {
            type T = long;
            a:T = 1;
            check("a", a, 1);
        };
        foo();

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefScope5().execute();
    }
}
