/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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

    public static def main(var args: Array[String](1)): void = {
        new ClosureBody2().execute();
    }
}
