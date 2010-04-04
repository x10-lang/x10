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
 * Array operations and points must be type checked.
 *
 * The expected result is that compilation must fail.
 * A val of type Point(3) should be statically known to not == a val
 * of type Point(2).
 *
 * @author kemal 4/2005
 * @author vj 9/2008
 */

public class ArrayTypeCheck4_MustFailCompile extends x10Test {

    public def run(): boolean = {

        val O = Dist.makeConstant([0..2, 0..3], here);
        val a1 = Array.make[int](O, (var p(i): Point): int => { return i; });

        x10.io.Console.OUT.println("1");

        val E = Dist.makeConstant(-1..-2, here);
        val a2  = a1 as Array[int](E);

        x10.io.Console.OUT.println("2");

        val D = Dist.makeUnique();
        val a3  = a2 as Array[int](D);

        x10.io.Console.OUT.println("3");

        var i: int = 1;
        var j: int = 2;
        var k: int = 0;

        val p = [i, j, k] as Point;
        val q = [i, j] as Point;
        val r = [i] as Point;

        // should be a compile time error. a Point(3) can never equal a Point(2)
        if (p == q)  
            return false;

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayTypeCheck4_MustFailCompile().execute();
    }
}
