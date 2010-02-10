// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION:
// <= constraints not yet supported
// overloading of local typedefs not supported

import harness.x10Test;


/**
 * A compilation unit may have one or more type definitions or class
 * or interface declarations with the same name, as long as the
 * definitions have distinct parameters according to the method
 * overloading rules (9.7.1).
 *
 * @author bdlucas 9/2008
 */

public class TypedefOverloading02 extends TypedefTest {

    public def run(): boolean = {
        
        type double(x: double) = double{self==x};
        type double(lo: double, hi: double) = double{lo <= self, self <= hi};

        var x:double = 1;
        var y:double(2) = 2;
        var z:double(0,5) = 3;

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading02().execute();
    }
}
