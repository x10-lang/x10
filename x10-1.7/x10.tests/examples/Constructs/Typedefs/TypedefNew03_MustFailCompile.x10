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

public class TypedefNew03 extends TypedefTest {

    public def run(): boolean = {
        
        class A(k:int) {
            def this():A{self.k==0} = property(0);
            def this(i:int):A{self.k==i} = property(i);
        }
        
        type T(i:int) = A{self.k==i};
        type T0 = A{self.k==0};
        type T1 = A{self.k==1};

        // not allowed - T has value parameter
        val t = new T(0);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefNew03().execute();
    }
}
