// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with methods, a closure with return type Void cannot have a
 * terminating expression.
 */

public class ClosureReturnType1_MustFailCompile extends ClosureTest {

    def foo() = {}

    public def run(): boolean = {
        
        val f = ():void => {foo(); 1};
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType1_MustFailCompile().execute();
    }
}
