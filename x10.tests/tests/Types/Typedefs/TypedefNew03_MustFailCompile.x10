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
 * An instance of a defined type with no type parameters and no value
 * parameters may be used to instantiate an instance of a type. The type
 * has the same constructors with the same signature as its defining
 * type; however, a constructor may not be invoked using a given defined
 * type name if the constructor return type is not a subtype of the
 * defined type.
 *
 * @author bdlucas 9/2008
 */

public class TypedefNew03_MustFailCompile extends TypedefTest {

    public def run(): boolean = {
        
        class A(k:long) {
            def this():A{self.k==0} = property(0);
            def this(i:long):A{self.k==i} = property(i);
        }
        
        type TVALUE(i:long) = A{self.k==i};
        type T0 = A{self.k==0};
        type T1 = A{self.k==1};

        // not allowed - TVALUE has value parameter
        val t = new TVALUE(0); // ERR ERR  [Semantic Error: Could not find type "TVALUE"., Semantic Error: No valid constructor found for T(x10.lang.Long{self==0}).]

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefNew03_MustFailCompile().execute();
    }
}
