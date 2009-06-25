// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * A Call may to either a method or a closure. The syntax is
 * ambiguous; the target must be typechecked to determine if it is the
 * name of a method or if it refers to a closure.
 *
 * @author bdlucas 8/2008
 */

public class ClosureCall2 extends ClosureTest {

    def f(x:int) = "method";
    val f = (x:String) => "closure";

    public def run(): boolean = {

        check("f(1)", f(1), "method");
        check("f(\"1\")", f("1"), "closure");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall2().execute();
    }
}
