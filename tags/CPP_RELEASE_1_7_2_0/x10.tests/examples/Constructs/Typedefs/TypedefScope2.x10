// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Type definitions may appear as class members or in the body of a
 * method, constructor, or initializer.
 *
 * @author bdlucas 8/2008
 */

public class TypedefScope2 extends TypedefTest {

    static type T = int;

    public def run(): boolean = {
        
        a:T = 1;
        check("a", a, 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefScope2().execute();
    }
}
