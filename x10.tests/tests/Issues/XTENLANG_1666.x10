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
 * @author yoav
 * Testing a bug in definite-assignment rules
 */
public class XTENLANG_1666 extends x10Test {
    public static def main(Rail[String]) {
        new XTENLANG_1666().execute();
    }
    var flag:Boolean;
    public def run(): boolean {	
        var z:long;
        z = flag || (z=3)>0 ? 3 : z++;

        var y:long;
        y = (flag || (y=3)>0) ? 3 : y++;  // the bug was in ParExpr (parenthesis expressions)


        var k:long;
        k = !(!(flag || (k=3)>0)) ? 3 : k++;

        z++;
        y++;
        k++;

        // I also had a bug with ParExpr in await:
        when (!false);
        when (true);

        return true;
    }
}
