// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * The closure body may refer to instances of enclosing classes using the
 * syntax C.this, where C is the name of the enclosing class.
 *
 * @author bdlucas 8/2008
 */

public class ClosureEnclosingScope4 extends ClosureTest {

    val a = 1;

    public def run(): boolean = {
        
        class C {
            val a = 2;
            class D {
                val a = 4;
                val sum = (()=>(ClosureEnclosingScope4.this.a+C.this.a+D.this.a+a))();
            }
        }

        check("new C().new D().sum", new C().new D().sum, 11);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureEnclosingScope4().execute();
    }
}
