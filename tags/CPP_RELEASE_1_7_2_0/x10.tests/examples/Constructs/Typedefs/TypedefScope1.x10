// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION:
// top-level typedefs not supported

import harness.x10Test;


/**
 * All type definitions are members of their enclosing package or
 * class.
 *
 * @author bdlucas 8/2008
 */

type T = int;

public class TypedefScope1 extends TypedefTest {

    public def run(): boolean = {
        
        a:T = 1;
        check("a", a, 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefScope1().execute();
    }
}
