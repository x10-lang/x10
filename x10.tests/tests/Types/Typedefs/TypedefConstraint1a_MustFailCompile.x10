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
 * @author bdlucas 9/2008
 */

public class TypedefConstraint1a_MustFailCompile extends TypedefTest {

    class X           {def name(): String = "X";}
    class Y extends X {def name(): String = "Y";}
    class Z extends Y {def name(): String = "Z";}

    public static type A1 = Z;
    public static type A2[T] = Z;
    public static type A3[T] {T==Y} = Z;

    public def run(): boolean {
        var a1:A1;
        var a11:A1[X]; // ShouldBeErr
        var a2:A2[X];
        var a3:A3[Y];
        var a4:A3[Z]; // ERR todo: better err msg: Could not find type "A3".
        var a5:A3[X]; // ERR

        return result;
    }

    public static def main(var args: Rail[String]): void {
        new TypedefConstraint1a_MustFailCompile().execute();
    }
}
