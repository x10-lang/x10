// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * As with a normal method, the closure may have a throws clause
 */


public class ClosureException1g extends ClosureTest {

    public def run(): boolean = {
        
        val g = (x:int) throws Exception => x;
        check("g(2)", g(2), 2);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureException1g().execute();
    }
}
