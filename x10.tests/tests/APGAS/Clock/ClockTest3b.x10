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
 * Clock test for barrier functions.
 * Alternate barrier version where parent activity terminates, and
 * finish is used to wait for the children
 * @author kemal 3/2005
 */
public class ClockTest3b extends x10Test {

    var value: long = 0;
    static val N: long = 32;

    public def run(): boolean = {
        clocked finish  {
            for (i in 0..(N-1)) clocked async {
                clocked async  finish async  { atomic value++; }
                Clock.advanceAll();
                chk(value == N);
                Clock.advanceAll();
                clocked async  finish async  { atomic value++; }
                Clock.advanceAll();
            }
        }
        chk(value == 2*N);
        return true;
    }

    public static def main(Rail[String]) {
        new ClockTest3b().executeAsync();
    }
}
