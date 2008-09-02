// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * A compilation unit may have one or more type definitions or class
 * or interface declarations with the same name, as long as the
 * definitions have distinct parameters according to the method
 * overloading rules (§9.7.1).
 *
 * @author bdlucas 9/2008
 */

public class TypedefOverloading1 extends TypedefTest {

    public def run(): boolean = {
        
        type Int(x: Int) = Int{self==x};
        type Int(lo: Int, hi: Int) = Int{lo <= self, self <= hi};

        var x:Int = 1;
        var y:Int(2) = 2;
        var z:Int(0,5) = 3;

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefOverloading1().execute();
    }
}

