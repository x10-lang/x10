// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.util.List;

/**
 * Example from spec, section 11.2 ("Function Literals")
 * If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */
import x10.util.ArrayList;
public class ClosureExample1 extends x10Test {

    def find[T](f:(T)=>Boolean, xs: List[T]):T {
        for (x in xs)
            if (f(x)) return x;
        return null;
    }

    val xs: List[Int]! = new ArrayList[Int]();
    
    val x: Int = find((x: Int) => (x>0), xs);

    public def run(): boolean = {
        // XXX just syntax and type check for now
        xs.add(1);
        xs.add(2);
        xs.add(3);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureExample1().execute();
    }
}
