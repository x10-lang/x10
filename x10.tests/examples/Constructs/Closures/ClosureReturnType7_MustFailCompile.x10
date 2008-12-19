// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * if the method does not return a value, the inferred type is Void
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType7_MustFailCompile extends ClosureTest {

    public def run(): boolean = {
        
        // inferred to be void
        val f = (x:int) => {};

        // should fail because f() is void
        val g = f(0);

        return result;
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType7_MustFailCompile().execute();
    }
}
