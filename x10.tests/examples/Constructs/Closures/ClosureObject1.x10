// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Closures, like all first-class functions in X10 are objects
 * (§4.6.1).
 *
 * @author bdlucas 8/2008
 */

public class ClosureObject1 extends ClosureTest {

    public def run(): boolean = {
        
        f:Object = ()=>1;
        check("(f as ()=>int)()", (f as ()=>int)(), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureObject1().execute();
    }
}
