// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Closure expressions have zero or more formal parameters
 *
 * @author bdlucas 8/2008
 */

public class ClosureFormalParameters1a extends ClosureTest {

    public def run(): boolean = {
        
        check("(()=>0)()", (()=>0)(), 0);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureFormalParameters1a().execute();
    }
}
