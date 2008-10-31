// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with a normal method, the closure may have a throws clause
 *
 * @author bdlucas 8/2008
 */


public class ClosureException1i extends ClosureTest {

    class E extends Exception {}

    public def run(): boolean = {
        
        class C[T] {val i = (){T<:Exception} throws T => 1;}
        check("new C[Exception]().i()", new C[Exception]().i(), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureException1i().execute();
    }
}
