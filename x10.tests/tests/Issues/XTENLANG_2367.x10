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

//OPTIONS: -STATIC_CHECKS

import x10.compiler.tests.*; // err markers
import harness.x10Test;

/**
 * See XTENLANG_2367
 * This ought to compile, but doesn't.  
 * @author bardb 1/2011
 */
public class XTENLANG_2367 extends x10Test { 
    public def run() = true;

    public static def main(Rail[String]) {
        new XTENLANG_2367().execute();
    }

    private static class Ex {
        var f : String = "";
        def example(x:Any){x != null} = {
            this.f = x.toString();
        }
    }
}

