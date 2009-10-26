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

public class ClosureBody1c extends ClosureTest {

    var x:int = 0;

    def x(x:int):void = {
        this.x=x;
    }

    def x() = x;

    public def run(): boolean = {
        
        // block terminated by return expression
        val h = ()=>{x(2);x()+1};
        check("h()", h(), 3);
        check("x after h()", x(), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureBody1c().execute();
    }
}
