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

public class TypedefConstraint1a extends TypedefTest {

    class X           {def name(): String = "X";}
    class Y extends X {def name(): String = "Y";}
    class Z extends Y {def name(): String = "Z";}

    public def run(): boolean = {
        
        type A[T]{T==Y} = T;
        a:A[Y] = new Y();
        check("a.name()", a.name(), "Y");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefConstraint1a().execute();
    }
}
