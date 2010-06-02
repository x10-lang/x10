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
 * The inferred type of a method or closure body is the least common
 * ancestor of the types of the expressions in return statements in the
 * body.
 *
 * @author bdlucas 8/2008
 */

public class ClosureReturnType4 extends ClosureTest {

    class A {
        def name(): String = "A";
    }

    class B extends A {
        def name(): String = "B";
    }

    class C extends A {
        def name(): String = "C";
    }

    public def run(): boolean = {
        
        val f = (x:int) => {if (x==0) return new B(); return new C();};
        val a0 = f(0);
        val a1 = f(1);
        check("a0.name()", a0.name(), "B");
        check("a1.name()", a1.name(), "C");

        return result;
    }


    public static def main(var args: Rail[String]): void = {
        new ClosureReturnType4().execute();
    }
}
