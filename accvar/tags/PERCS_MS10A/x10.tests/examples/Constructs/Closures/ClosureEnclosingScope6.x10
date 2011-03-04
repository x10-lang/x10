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
import x10.compiler.NonEscaping;

/**
 * The closure body may refer to instances of enclosing classes using the
 * syntax C.this, where C is the name of the enclosing class.
 *
 * @author bdlucas 8/2008
 */

public class ClosureEnclosingScope6 extends ClosureTest {

    val a = 1;

    class C {
        def a() = 2;
        class D {
            @NonEscaping final def a() = 4;
            def sum() = (()=>(
				ClosureEnclosingScope6.this.a
				+
					C.this.a()+
					D.this.a()+a()))();
        }
    }

    public def run(): boolean = {
        
        check("new C().new D().sum", new C().new D().sum(), 11);

        return result;
    }

    public static def main(var args: Array[String](1)): void = {
        new ClosureEnclosingScope6().execute();
    }
}
