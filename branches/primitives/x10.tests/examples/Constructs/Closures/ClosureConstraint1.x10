// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with methods, a closure may declare a where clause to constraint
 * the actual parameters with which it may be invoked.
 *
 * @author bdlucas 8/2008
 */


public class ClosureConstraint1 extends ClosureTest {

    public def run(): boolean = {
        
        val f = (x:int){x==1} => x;
        val one = 1;
        check("f(1)", f(1), 1);
        check("f(one)", f(one), 1);

        val g = (x:int,y:int){x==1&&y==-1} => x+y;
        check("g(1,-1)", g(1,-1), 0);
        check("g(one,-1)", g(one,-1), 0);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureConstraint1().execute();
    }
}
