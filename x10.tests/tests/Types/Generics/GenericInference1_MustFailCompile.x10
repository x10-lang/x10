
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
 * A call to a polymorphic method, closure, or staticructor may omit
 * the explicit type arguments. If the method has a type parameter T,
 * the type argument corresponding to T is inferred to be the least
 * common ancestor of the types of any formal parameters of type T.
 *
 * @author bdlucas 8/2008
 */

public class GenericInference1_MustFailCompile extends GenericTest {

    class V           {static name = "V";};
    class W extends V {static name = "W";}
    class X extends V {static name = "X";};
    class Y extends X {static name = "Y";};
    class Z extends X {static name = "Z";};

    def m[T](){T<:X} =
        T.name; // ERR: Cannot access static field of a type parameter	 Type Parameter: T

    public def run(): boolean {

        // must fail compile
        val a =
            m(); // ERR:  Method m[T](){}[T <: GenericInference1_MustFailCompile.X]: x10.lang.String{self=="X"} in GenericInference1_MustFailCompile{self==GenericInference1_MustFailCompile#this} cannot be called with arguments (); Cannot infer type for type parameter T.
                    // used to be: Method m[T](){}[T <: GenericInference1_MustFailCompile.X]: x10.lang.String{self=="X"} in GenericInference1_MustFailCompile{self==GenericInference1_MustFailCompile#this} cannot be called with arguments (); Could not infer type for type parameter T.
                    // now: (Diagnostic) No constraint on type parameters. Returning Any instead of throwing an exception.
        genericCheck("a", a, "hi");

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new GenericInference1_MustFailCompile().execute();
    }
}
