// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * It is a static error if a checked exception is thrown that is not
 * declared in the throws clause.
 *
 * @author bdlucas 8/2008
 */

public class ClosureException2_MustFailCompile extends ClosureTest {

    public def run(): boolean = {
        
        try {
            // undeclared exception should fail
            val f = () => {throw new Exception();};
        } catch (e:Exception) {}

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ClosureException2_MustFailCompile().execute();
    }
}
