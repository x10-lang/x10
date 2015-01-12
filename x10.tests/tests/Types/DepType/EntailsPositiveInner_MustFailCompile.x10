/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;

/**
 * A class with parameters, Test, is defined as an inner class. Check
 * that a type Test(:i==j) can be defined.
 *
 * @author vj
 */
public class EntailsPositiveInner_MustFailCompile extends x10Test {
    class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii,self.j==jj} = { property(ii,jj);}
    }

    public def run(): boolean = {
        var x: Test{self.i==self.j} = new Test(1n,2n); // ERR should fail
        return true;
    }
    public static def main(var args: Rail[String]): void = {
        new EntailsPositiveInner_MustFailCompile().execute();
    }
}
