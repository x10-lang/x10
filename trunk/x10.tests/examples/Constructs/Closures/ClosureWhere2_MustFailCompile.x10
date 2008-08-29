// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with methods, a closure may declare a where clause to constraint
 * the actual parameters with which it may be invoked.
 *
 * @author bdlucas 8/2008
 */

public class ClosureWhere2_MustFailCompile extends ClosureTest {

    public def run(): boolean = {
        
        val f = (x:int){x==1}=>x;

        var a:int = 1;
        f(a); // fails compilation

        return result;
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureWhere2_MustFailCompile().execute();
    }
}
