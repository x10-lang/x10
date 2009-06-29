// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * The closure body has the same syntax as a method body; it may be
 * either an expression, a block of statements, or a block terminated
 * by an expression to return.
 */

public class ClosureBody1 extends ClosureTest {

    var x:int = 0;

    def x(x:int):void = {
        this.x=x;
    }

    def x() = x;

    public def run(): boolean = {
        
        // expression
        val f = ()=>1+1;
        check("f()", f(), 2);

        // block
	//        val g = ()=>{x(1);};
	//        g();
	//        check("x after g()", x(), 1);

        // block terminated by return expression
	//        val h = ()=>{x(2);x()+1};
	//        check("h()", h(), 3);
	//        check("x after h()", x(), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureBody1().execute();
    }
}
