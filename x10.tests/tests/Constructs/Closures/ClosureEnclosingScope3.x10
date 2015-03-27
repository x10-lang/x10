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
 * The closure body may refer to instances of enclosing classes using the
 * syntax C.this, where C is the name of the enclosing class.
 *
 * @author bdlucas 8/2008
 */

public class ClosureEnclosingScope3 extends x10Test {

    val a = 1;

    class C {
        val a = 2;
        class D {
            val a = 4;
            def sum() = (()=>(ClosureEnclosingScope3.this.a
            		+C.this.a
            		+D.this.a
            		+a))();
        }
    }

    public def run(): boolean {
        
        chk(new C().new D().sum() == 11, "new C().new D().sum");

        return true;
    }

    public static def main(Rail[String]) {
        new ClosureEnclosingScope3().execute();
    }
}
