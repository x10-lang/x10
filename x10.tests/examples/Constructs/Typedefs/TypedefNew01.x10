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

public class TypedefNew01 extends TypedefTest {

    public def run(): boolean = {
        
        class A(k:int) {
            def this():A{self.k==0} = property(0);
            def this(i:int):A{self.k==i} = property(i);
        }
        
        type T(i:int) = A{k==i};
        type T0 = A{k==0};
        type T1 = A{k==1};

        // sanity check
        val a1 = new A();
        val a2 = new A(0);

        // allowed
        val t1:A{self.k==0} = new T0();  // A{k==0} <: A{k==0}
        val t2:A{self.k==0} = new T0(0); // A{k==0} <: A{k==0}
        val t3:A{self.k==1} = new T1(1); // A{k==1} <: A{k==1}

        return result;
    }

    public static def main(Rail[String]) {
        new TypedefNew01().execute();
    }
}
