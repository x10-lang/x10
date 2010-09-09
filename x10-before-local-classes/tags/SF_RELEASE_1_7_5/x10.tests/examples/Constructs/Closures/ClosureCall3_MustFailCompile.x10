// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * It is a static error if a call may resolve to both a closure call or
 * to a method call.
 *
 * @author bdlucas 8/2008
 */

public class ClosureCall3_MustFailCompile extends x10Test {

    def f(x:int) = "floor wax";
    val f = (x:int) => "desert topping";

    public def run(): boolean = {
        check("f(1)", f(1), "comedic hilarity");
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall3_MustFailCompile().execute();
    }
}
