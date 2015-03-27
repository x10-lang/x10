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

import harness.x10Test;


/**
 * A call to a polymorphic method, closure, or constructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *
 * @author bdlucas 8/2008
 */

public class GenericInference4 extends GenericTest {

    class V           {static name = "V";};
    class W extends V {static name = "W";};
    class X extends V {static name = "X";};
    class Y extends X {static name = "Y";};
    class Z extends X {static name = "Z";};

    def m[T,U](t:T,u:U){T<:X && U<:X} = X.name + X.name;

    public def run(): boolean {

        val d = m(new Y(), new Z());
        genericCheck("d", d, "XX");

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new GenericInference4().execute();
    }
}
