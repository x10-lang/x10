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
 * @author bdlucas 10/2008
 */

import x10.io.Writer;
import x10.io.IOException;
    
class XTENLANG_35 extends x10Test {

    def foo(os:Writer, buf:Rail[Byte]) {
        try {
            os.write(buf, 0, buf.size);
        } catch (Exception) {}
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_35().execute();
    }
}
