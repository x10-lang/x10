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

public class ClosureCall1d extends ClosureTest {

    public def run(): boolean = {

        val d = [T,U](t:T,u:U) => (t+t)+(u+u);
        check("d(\"1\",1)", d("1",1), "112");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1d().execute();
    }
}
