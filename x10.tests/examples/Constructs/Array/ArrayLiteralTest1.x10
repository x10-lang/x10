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
import x10.array.*;

/**
 * Array Literal test. Same as ArrayLiteralTest except that the declared type of the array is more precise.
 */
public class ArrayLiteralTest1 extends x10Test {

    public def run(): boolean = {

        val r = [1, 2, 3];
        val a = [1 as Int{self!=0},2 as Int{self!=0},3 as Int{self!=0}];
        var sumr:int=0;
        var suma:int=0;
        for (i in a) suma += i;
        for (i in r) sumr +=i;
        return suma==6 && sumr==6;
    }

    public static def main(Rail[String]): void = {
        new ArrayLiteralTest1().execute();
    }
}
