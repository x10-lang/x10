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
 * @author yoav
 */

public class XTENLANG_1466 extends x10Test {

    public def run(): boolean {
        val f:XTENLANG_1466 = true ? null : new XTENLANG_1466();
        return f == null;
    }

    public static def main(Rail[String]) {
        new XTENLANG_1466().execute();
    }
}
