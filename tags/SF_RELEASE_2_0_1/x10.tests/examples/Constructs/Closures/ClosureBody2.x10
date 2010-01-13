// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * The body of the closure is evaluated when the closure is invoked by
 * a call expression (§12.8), not at the closure’s place in the
 * program text.
 *
 * @author bdlucas 8/2008
 */

public class ClosureBody2 extends ClosureTest {

    var x:int = 0;

    def x(x:int):void = {
        this.x=x;
    }


    public def run(): boolean = {
        
        // not evaluated here
        val f = () => {x(1)};
        check("x after defn", x, 0);

        // evaluated here
        f();
        check("x after f()", x, 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureBody2().execute();
    }
}
