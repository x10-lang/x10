// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test. *

import harness.x10Test;


/**
 * Closure expressions have zero or more formal parameters
 */

public class ClosureFormalParameters1d extends ClosureTest {

    public def run(): boolean = {
        
        check("((i:String,j:int)=>i+j)(\"1\",1)", ((i:String,j:int)=>i+j)("1",1), "11");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureFormalParameters1d().execute();
    }
}
