/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Check that the type Test(:self.j==1) is a subtype of Test(:self.j==j) when j is of type int(:self==1).
 *
 * @author vj
 */
public class EntailsPositiveInnerMustPass extends x10Test {
    class Test(i:int, j:int) {
        public def this(ii:int, jj:int):Test{self.i==ii&&self.j==jj} = {
            property(ii,jj);
        }
    }

    public def run(): boolean = {
        val j: int{self==1n} = 1n;
        var x: Test{self.j==j} = new Test(1n,1n);
        return true;
    }
    public static def main(var args: Rail[String]): void = {
        new EntailsPositiveInnerMustPass().execute();
    }
}
