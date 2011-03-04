// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.util.List;

/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class ClosureExample1 extends x10Test {

    def find[T](f:(T)=>Boolean, xs: List[T]):T {
        for (x in xs)
            if (f(x)) return x;
        return null;
    }

    val xs: List[Int] = [1,2,3];
    val x: Int = find((x: Int) => (x>0), xs);

    public def run(): boolean = {
        // XXX just syntax and type check for now
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureExample1().execute();
    }
}
