// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with a normal method, the closure may have a throws clause
 *
 * @author bdlucas 8/2008
 */


public class ClosureException1j extends ClosureTest {

    class E extends Exception {}

    public def run(): boolean = {
        
        val j = [T](){T<:Exception} throws T, Exception => 1;
        check("j[E]()", j[E](), 1);
        check("j[Exception]()", j[Exception](), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureException1j().execute();
    }
}
