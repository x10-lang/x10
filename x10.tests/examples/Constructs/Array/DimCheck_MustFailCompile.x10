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

//LIMITATION:
//This check is not being done by the compiler currently.
import harness.x10Test;

/**
 * Dimensionality of array initializer must be checked.
 *
 * @author vj 12 2006
 */

public class DimCheck_MustFailCompile extends x10Test {

    public def run(): boolean = {
        // The compiler should check the type of the distribution in the array constructor.
        // If  the type does not specify a constraint on the arity of the underlying region
        // then the initializer cannot specify the arity of the point. Otherwise the arity of the
        // point must be the same as the arity of the distribution.
        var a1: Array[int] = new Array[int](Dist.makeConstant((0..2)*(0..3), here), ([i]:Point)=> i);
        x10.io.Console.OUT.println(a1);
        return true;
    }

    public static def main(var args: Array[String](1)): void = {
        new DimCheck_MustFailCompile().execute();
    }
}
