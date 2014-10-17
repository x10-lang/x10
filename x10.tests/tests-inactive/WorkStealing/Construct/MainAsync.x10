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
//OPTIONS: -WORK_STEALING=true

import harness.x10Test;

/*
 * There is only one async from main's path
 * (But the x10Test has the finish scope)
 * Use busy waiting to wait the result
 */

public class MainAsync extends x10Test{
    
    var flag: boolean = false;
    static N: long = 1000000000;
    
    public def run() {
        var b: boolean = false;
        async  { atomic { this.flag = true; } }
        for (var i: long = 0; i < N*100; i++) {
            System.sleep(1);
            atomic { b = flag; }
            if (b) break;
        }
        return b;
    }

    public static def main(Rail[String]) {
        new MainAsync().execute();
    }
    
    
}