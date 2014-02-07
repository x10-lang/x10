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
import x10.regionarray.*;

/**
 * Cannot bind point components in array declaration.
 * Yoav todo: I don't understand why you can't explode an Array[T] into components of type T?
 *
 * @author igor, 1/2006
 */

public class ArrayPointBinding_MustFailCompile extends x10Test {

    public def run(): boolean = {
        { val p[i,j]:Array[Point]{rank==1,size==2} = new Array[Point](2); }
        { val p[i,j]:Array[Point]{size==2} = new Array[Point](2); } // ERR: Semantic Error: You can exploded the Array only if its has the constraint {rank==1,size=2}
        { val p[i,j]:Array[Point]{rank==1} = new Array[Point](2); } // ERR: Semantic Error: You can exploded the Array only if its has the constraint {rank==1,size=2}
        { val p[i,j]:Array[Point] = new Array[Point](2); }          // ERR: Semantic Error: You can exploded the Array only if its has the constraint {rank==1,size=2}

        val p[i,j] = new Array[Point](2);
        p(0) = [1,2];
        p(1) = [3,4];

        return (i(0) == 1 && j(1) == 4);  
    }

    public static def main(args: Rail[String]): void = {
        new ArrayPointBinding_MustFailCompile().execute();
    }
}

