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

import harness.x10Test;

/*
 * Simple Finish-Async test
 */

public class FinishAsync extends x10Test{
    
    var flag: boolean = false;
    
    public def run() {
        finish {
            async { atomic { this.flag = true; } }
        }
        return flag;
    }

    public static def main(Rail[String]) {
        new FinishAsync().execute();
    }
    
    
}