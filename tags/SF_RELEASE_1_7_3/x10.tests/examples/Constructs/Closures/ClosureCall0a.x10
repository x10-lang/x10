// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: closure type params

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *
 * @author nystrom 8/2008
 */

public class ClosureCall0a extends ClosureTest {

    public def run(): boolean = {

        val a = ([T](x:T) => x)("hi");
        check("a", a, "hi");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall0a().execute();
    }
}
