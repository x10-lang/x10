// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with methods, a closure with return type Void cannot have a
 * terminating expression.
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType5_MustFailCompile extends ClosureTest {

    def x() = 1;

    public def run(): boolean = {
        
        val foo = ():void => x();
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType5_MustFailCompile().execute();
    }
}
