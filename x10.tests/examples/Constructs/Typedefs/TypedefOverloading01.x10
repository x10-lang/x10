// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

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

    static class D(x: int, y: int) {
        def this(a: int, b: int): D{x==a,y==b} {
            property(a,b);
        }
    }

    static type C(a: int) = D{x==a};
    static type C(a: int, b: int) = C(a){self.y==b};

    public def run(): boolean = {
        var x:D = new D(0,0);
        var y:C(2) = new D(2,3);
        var z:C(0,5) = new D(0,5);
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading01().execute();
    }
}
