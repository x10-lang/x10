// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *
 * @author bdlucas 8/2008
 */

public class ClosureCall1b extends ClosureTest {

    public def run(): boolean = {

        val b = [T](t:T) => t+t;
        check("b(\"1\")", b("1"), "11");
        check("b(1)", b(1), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1b().execute();
    }
}
