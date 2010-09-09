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
