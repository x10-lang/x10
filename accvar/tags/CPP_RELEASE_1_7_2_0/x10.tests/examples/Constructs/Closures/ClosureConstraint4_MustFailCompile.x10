// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with methods, a closure may declare a where clause to constraint
 * the actual parameters with which it may be invoked.
 *
 * @author bdlucas 8/2008
 */

public class ClosureConstraint4_MustFailCompile extends ClosureTest {

    public def run(): boolean = {
        
        val f = (x:int){x==1}=>x;

        val d = 0;
        f(d); // fails compilation

        return result;
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureConstraint4_MustFailCompile().execute();
    }
}
