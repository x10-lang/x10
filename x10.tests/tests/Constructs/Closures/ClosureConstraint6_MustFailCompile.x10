/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;


/**
 * "Constraint" constraints on var parameters are not allowed.
 *
 * @author bdlucas 8/2008
 */

public class ClosureConstraint6_MustFailCompile extends x10Test {

    public def run(): boolean = {
        
        // not allowed
        val f:Any =
            (var x:long){x==1} => x; // ERR

        return true;
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureConstraint6_MustFailCompile().execute();
    }
}
