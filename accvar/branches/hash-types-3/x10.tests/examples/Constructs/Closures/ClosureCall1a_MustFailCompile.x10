/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

//LIMITATION: closure type params

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *
 * @author vj 9/2009 -- Closures are no longer permitted to take type parameters.
 * @author bdlucas 8/2008
 */

public class ClosureCall1a_MustFailCompile extends ClosureTest {

    class V           {const name = "V";}
    class W extends V {const name = "W";}
    class X extends V {const name = "X";}
    class Y extends X {const name = "Y";}
    class Z extends X {const name = "Z";}

    public def run(): boolean = {

        // must fail compile
        val a = ([T](){T<:X} => T.name)();
        check("a", a, "hi");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1a_MustFailCompile().execute();
    }
}
