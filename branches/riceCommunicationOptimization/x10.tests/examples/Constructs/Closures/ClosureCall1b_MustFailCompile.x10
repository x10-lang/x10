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
	assert y.equals("Y") : "Y expected, obtained " + y;
        val z = v(new Z());
	assert z.equals("Z") : "Z expected, obtained " + z;
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1b_MustFailCompile().execute();
    }
}
