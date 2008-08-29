// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with methods, a closure with return type Void cannot have a
 * terminating expression.
 */

public class ClosureReturnType6_MustFailCompile extends ClosureTest {

    def x() = 1;

    public def run(): boolean = {
        
        val foo = ():void => x();
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType6_MustFailCompile().execute();
    }
}
