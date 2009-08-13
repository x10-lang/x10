// (C) Copyright IBM Corporation 2008
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

    public def run(){
        val foo = ():void => x();
        return true;
    }

    public static def main(Rail[String]) {
        new ClosureReturnType5_MustFailCompile().execute();
    }
}
