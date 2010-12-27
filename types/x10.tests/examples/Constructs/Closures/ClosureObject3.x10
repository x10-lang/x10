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
 * The function type [X1, . . ., Xk](x1: T1 . . ., xn: Tn) => S may be
 * considered equivalent to an interface type with a method:
 * operator this[X1, . . ., Xk](x1: Y1, . . ., xn: Yn): Z;
 *
 * A closure call e(. . .) is shorthand for a method call e(. . .).
 *
 * @author bdlucas 8/2008
 */

public class ClosureObject3 extends ClosureTest {

    public def run(): boolean = {
        
        val f:(int)=>int = (i:int) => i+1;
        check("f(1)", f(1), 2);
        check("f(1)", f(1), 2);

        return result;
    }

    public static def main(var args: Array[String](1)): void = {
        new ClosureObject3().execute();
    }
}
