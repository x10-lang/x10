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

import x10.util.Box;
import x10.util.List;

/**
 * Example from spec, section 11.2 ("Function Literals")
 * If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */
/* changed return type to Box[T] by mtake */
import x10.util.ArrayList;
public class ClosureExample1 extends x10Test {

    // def find[T](f:(T)=>Boolean, xs: List[T]):T {
    static def find[T](f:(T)=>Boolean, xs: List[T]): Box[T] {
        for (x in xs)
            // if (f(x)) return x;
            if (f(x)) return new Box[T](x);
        return null;
    }

    val xs  = new ArrayList[Long]();
    
    // val x: Int = find((x: Long) => (x>0), xs);
    val x: Box[Long] = find((x: Long) => (x>0), xs);

    public def run(): boolean {
        // XXX just syntax and type check for now
        xs.add(1);
        xs.add(2);
        xs.add(3);
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new ClosureExample1().execute();
    }
}
