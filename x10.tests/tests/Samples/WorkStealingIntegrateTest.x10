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
//OPTIONS: -WORK_STEALING=true
// SOURCEPATH: x10.dist/samples/work-stealing

import harness.x10Test;

public class WorkStealingIntegrateTest extends x10Test {
    public def run():boolean {
        val res = Integrate.computeArea(0, 64);
        return precision.is_equal(res, 4196352.000021513551474);
    }

    public static def main(args:Rail[String]) {
        new WorkStealingIntegrateTest().execute();
    }
}
