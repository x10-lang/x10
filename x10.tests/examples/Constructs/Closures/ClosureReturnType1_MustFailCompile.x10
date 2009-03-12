// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with methods, a closure with return type Void cannot have a
 * terminating expression.
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType1_MustFailCompile extends ClosureTest {

    def foo() = {}

    public def run(): boolean = {
        val f = ():void => {foo(); 1};
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType1_MustFailCompile().execute();
    }
}
