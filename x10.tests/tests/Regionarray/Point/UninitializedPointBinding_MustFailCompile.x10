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
import x10.regionarray.*;

/**
 * Cannot bind components of an uninitialized point.
 *
 * @author igor, 1/2006
 */

public class UninitializedPointBinding_MustFailCompile extends x10Test {

    public def run(): boolean = {

        var p[i,j]: Point; // ERR: Syntax Error: An exploded point must have an initializer.
        p = [1,2];

        return (i == 1 && j == 2); // ERR ERR: "i" may not have been initialized "j" may not have been initialized
    }

    public static def main(args: Rail[String]): void = {
        new UninitializedPointBinding_MustFailCompile().execute();
    }
}

