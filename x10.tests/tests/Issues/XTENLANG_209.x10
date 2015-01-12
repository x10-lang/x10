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
 * @author bdlucas 12/2008
 */

public class XTENLANG_209 extends x10Test {

    public operator this + (c: int) = this;
    
    public def run():boolean {
        val i = 0n;
        val sum = this + i;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_209().execute();
    }
}
