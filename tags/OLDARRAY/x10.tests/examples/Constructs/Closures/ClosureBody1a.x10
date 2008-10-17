// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * The closure body has the same syntax as a method body; it may be
 * either an expression, a block of statements, or a block terminated
 * by an expression to return.
 *
 * @author bdlucas 8/2008
 */

public class ClosureBody1a extends ClosureTest {

    public def run(): boolean = {
        
        // expression
        val f = ()=>1+1;
        check("f()", f(), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureBody1a().execute();
    }
}
