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
 * 9/2009 -- Closures are no longer permitted to take type parameters.
 */

public class ClosureCall0b_MustFailCompile extends ClosureTest {

    public def run(): boolean = {

        // Omega = (\x.x x) (\x.x x)
        val f = [T](x:T){T <: T=>T} => x(x);
        val Omega = f(f);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall0b_MustFailCompile().execute();
    }
}
