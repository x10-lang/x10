/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

//LIMITATION: closure type params
// SHOULD_NOT_PARSE: closures with type parameters are not supported in the current X10 syntax.

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *
 *// Closures are no longer permitted to take type parameters.
 
 * @author bdlucas 8/2008
 */

public class ClosureCall1b_MustFailCompile extends x10Test {

    class V           {val name = "V";}
    class W extends V {val name = "W";}
    class X extends V {val name = "X";}
    class Y extends X {val name = "Y";}
    class Z extends X {val name = "Z";}

    public def run(): boolean = {
	val v = [T](t:T){T<:X} => t.name;
        val y = v(new Y());
	chk(y.equals("Y"), "Y expected, obtained " + y);
        val z = v(new Z());
	chk(z.equals("Z"), "Z expected, obtained " + z);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1b_MustFailCompile().execute();
    }
}
