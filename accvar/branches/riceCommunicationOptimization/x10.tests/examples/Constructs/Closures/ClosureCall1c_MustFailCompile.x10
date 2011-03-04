// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: closure type params

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *// Closures are no longer permitted to take type parameters.
 * @author bdlucas 8/2008
 */

public class ClosureCall1c_MustFailCompile extends ClosureTest {

    class V           {const name = "V";}
    class W extends V {const name = "W";}
    class X extends V {const name = "X";}
    class Y extends X {const name = "Y";}
    class Z extends X {const name = "Z";}

    public def run(): boolean = {

        val v = new V();
        val w = new W();
        val x = new X();
        val y = new Y();
        val z = new Z();

        val vz = ([T](t1:T,t2:T){T<:V} => T.name)(v,z);
        val wz = ([T](t1:T,t2:T){T<:V} => T.name)(w,z);
        val xy = ([T](t1:T,t2:T){T<:V} => T.name)(x,y);
        val yz = ([T](t1:T,t2:T){T<:V} => T.name)(y,z);
        val yy = ([T](t1:T,t2:T){T<:V} => T.name)(y,y);

        check("vz", vz, "V");
        check("wz", wz, "V");
        check("xy", xy, "X");
        check("yz", yz, "X");
        check("yy", yy, "Y");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1c_MustFailCompile().execute();
    }
}
