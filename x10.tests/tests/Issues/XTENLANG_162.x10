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

import x10.io.StringWriter;
import x10.io.Printer;
    
class XTENLANG_162 extends x10Test {

    public def run():boolean {
    
        val os = new StringWriter();
        val ps = new Printer(os);
    
        ps.printf("hi");
        x10.io.Console.OUT.println(os.result());

        return true;
    }
    
    public static def main(Rail[String]) {
        new XTENLANG_162().execute();
    }
}
