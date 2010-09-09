// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As objects, the closure body may refer to this, which is a
 * reference to the current function, and use it to invoke the closure
 * recursively.
 *
 * @author bdlucas 8/2008
 */

public class ClosureObject2 extends ClosureTest {

    public def run(): boolean = {
        
        val f = (i:int) => (i>1? this(i-1)+this(i-2) : 1);
        check("f(5)", f(5), 120);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureObject2().execute();
    }
}
