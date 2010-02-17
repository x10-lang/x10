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
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class ClosureExample4 extends ClosureTest {

    def fib(n: Int): Int {
        val f = (n: Int) : Point => {
//            if (n < 1) return new Point(1,0);
            if (n < 1) return Point.make(1,0);
            val (na,n1): Point = this(n-1);
            Point.make(n+n1,na)
        };
        val (nb,n1): Point = f(n);
        return nb;
    }

    public def run(): boolean = {
        
        // XXX just syntax and type check for now

        return true;
    }



    public static def main(var args: Rail[String]): void = {
        new ClosureExample4().execute();
    }
}
