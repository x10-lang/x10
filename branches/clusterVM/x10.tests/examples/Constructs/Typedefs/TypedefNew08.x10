// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * An instance of a defined type with no type parameters and no value
 * parameters may be used to instantiate an instance of a type. The type
 * has the same constructors with the same signature as its defining
 * type; however, a constructor may not be invoked using a given defined
 * type name if the constructor return type is not a subtype of the
 * defined type.
 *
 * @author bdlucas 9/2008
 */

public class TypedefNew08 extends TypedefTest {

    public def run(): boolean = {
        
        class X {}
        class Y {}

        class A[C] {
            def this() = {};
            def this(i:int):A[C]{C<:X} = {};
        }
        
        type T[C] = A[C];
        type TX = A[X];
        type TY = A[Y];

        // sanity check
        val a1 = new A[X]();
        val a2 = new A[X](0);

        // allowed
        val t1:A[X] = new TX();   // A[X] <: A[X]
        val t3:A[X] = new TX(0);  // A[X] <: A[X}{X<:X}
        val t2:A[Y] = new TY();   // A[Y] <: A[Y]

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefNew08().execute();
    }
}
