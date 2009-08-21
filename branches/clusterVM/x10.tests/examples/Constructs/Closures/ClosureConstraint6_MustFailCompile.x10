// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * "Constraint" constraints on var parameters are not allowed.
 *
 * @author bdlucas 8/2008
 */

public class ClosureConstraint6_MustFailCompile extends ClosureTest {

    public def run(): boolean = {
        
        // not allowed
        val f = (var x:int){x==1} => x;

        return result;
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureConstraint6_MustFailCompile().execute();
    }
}
