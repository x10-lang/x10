// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Example from spec. If changes need to be made to this code to make
 * it a valid example, update the spec accordingly.
 *
 * @author bdlucas 8/2008
 */

public class ClosureExample1 extends ClosureTest {

    def find[T](f: T => Boolean, xs: List[T]): T {
        for (x: T in xs)
            if (f(x)) return x;
        null
    }

    xs: List[Int] /*= ...*/;
    x: Int = find((x: Int) => (x>0), xs);


    public def run(): boolean = {
        
        // XXX just syntax and type check for now

        return result;
    }



    public static def main(var args: Rail[String]): void = {
        new ClosureExample1().execute();
    }
}
