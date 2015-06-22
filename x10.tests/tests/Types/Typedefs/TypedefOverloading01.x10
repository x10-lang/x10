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
 * A compilation unit may have one or more type definitions or class
 * or interface declarations with the same name, as long as the
 * definitions have distinct parameters according to the method
 * overloading rules (9.7.1).
 *
 * @author bdlucas 9/2008
 */

public class TypedefOverloading01 extends TypedefTest {

    static class D(x: long, y: long) {
        def this(a: long, b: long): D{self.x==a,self.y==b} {
            property(a,b);
        }
    }

    static type C(a: long) = D{x==a};
    static type C(a: long, b: long) = C(a){self.y==b};

    public def run(): boolean {
        var x:D = new D(0,0);
        var y:C(2) = new D(2,3);
        var z:C(0,5) = new D(0,5);
        return result;
    }

    public static def main(var args: Rail[String]): void {
        new TypedefOverloading01().execute();
    }
}
