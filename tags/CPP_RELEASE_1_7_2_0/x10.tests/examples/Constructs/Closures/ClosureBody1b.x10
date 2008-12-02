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

public class ClosureBody1b extends ClosureTest {

    var x:int = 0;

    def x(x:int):void = {
        this.x=x;
    }

    def x() = x;

    public def run(): boolean = {
        
        // block
        val g = ()=>{x(1);}; // void
        g();
        check("x after g()", x(), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureBody1b().execute();
    }
}
