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

public class ClosureCall1c extends ClosureTest {

    public def run(): boolean = {

        class V           {static val name = "V";};
        class W extends V {static val name = "W";}
        class X extends V {static val name = "X";};
        class Y extends X {static val name = "Y";};
        class Z extends X {static val name = "Z";};

        val v = new V();
        val w = new W();
        val x = new X();
        val y = new Y();
        val z = new Z();

        val c = [T](t1:T,t2:T) => T.name;

        check("c(v,z)", c(v,z), "V");
        check("c(w,z)", c(v,z), "V");
        check("c(x,y)", c(x,y), "X");
        check("c(y,z)", c(y,z), "X");
        check("c(y,y)", c(y,z), "Y");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureCall1c().execute();
    }
}
