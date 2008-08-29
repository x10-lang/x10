// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with a normal method, the closure may have a throws clause
 */


public class ClosureException1i extends ClosureTest {

    class E extends Exception {}

    public def run(): boolean = {
        
        val i = [T]() throws T => 1;
        check("i[Exception]()", i[Exception](), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureException1i().execute();
    }
}
