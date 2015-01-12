
/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

//LIMITATION: closure type params
// SHOULD_NOT_PARSE: closures with type parameters are not supported in the current X10 syntax.

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *// Closures are no longer permitted to take type parameters.
 * @author bdlucas 8/2008
 */

public class ClosureCall1c_MustFailCompile extends x10Test {

    class V           {public static val name = "V";}
    class W extends V {public static val name = "W";}
    class X extends V {public static val name = "X";}
    class Y extends X {public static val name = "Y";}
    class Z extends X {public static val name = "Z";}

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

        chk(vz.equals("V"), vz");
        chk(wz.equals("V"));
        chk(xy.equals("X"));
        chk(yz.equals("X"));
        chk(yy.equals("Y"));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1c_MustFailCompile().execute();
    }
}
