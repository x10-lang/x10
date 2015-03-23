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
 * If the type definition is a static member of a class or interface C,
 * then the type definition defines the type constructor C.X and C.X[S1,
 * . . ., Sm](e1, . . ., en) is a type.
 *
 * @author bdlucas 8/2008
 */

public class TypedefScope6 extends TypedefTest {

    interface X {
        static type T = long;
    }

    public def run(): boolean {
        
        a:X.T = 1;
        check("a", a, 1);

        b:TypedefScope6.X.T = 1;
        check("b", b, 1);

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new TypedefScope6().execute();
    }
}
