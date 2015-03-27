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
import x10.regionarray.Region;

/**
 * Check that the use of this violating constructor context restrictions
 * fails to compile.
 *
 * @author pvarma
 */
public class ThisInConstructorReturn_MustFailCompile extends x10Test {
    class Test(R1:Region) {
        val a:Region = Region.make(1,10);
        public def this():Test{self.R1==this.a} {
            property(this.a); // ERR
        }
    }

    public def run(): boolean {
        var t: Test = new Test();
        return t.R1==t.a;
    }
    public static def main(var args: Rail[String]): void {
        new ThisInConstructorReturn_MustFailCompile().execute();
    }
}
