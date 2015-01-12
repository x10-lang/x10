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

import harness.x10Test;

/**
 * @author bdlucas 9/2008
 */

public class TypedefConstraint2b extends TypedefTest {

    class X           {static name = "X";}
    class Y extends X {static name = "Y";}
    class Z extends Y {static name = "Z";}

    class FOO[T]{T<:X} {
        //val name = T.name;
    }

    public def run(): boolean = {
        
        type B[T]{T<:Y} = FOO[T];
        b:B[Y] = new FOO[Y]();
        //check("b.name", b.name, "Y");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint2b().execute();
    }
}
