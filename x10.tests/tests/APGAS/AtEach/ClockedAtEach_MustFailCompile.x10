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
import x10.regionarray.*;

/**
 * clocked ateach is not a valid construct
 */
public class ClockedAtEach_MustFailCompile extends x10Test {

    public def run():boolean {
        clocked finish {
            val D_Base = Dist.makeUnique();
            val P = 4;
            clocked ateach (z in D_Base)  { // ERR: Syntax error: Token "async" expected after this input
                Clock.advanceAll();
            }
        }
        return true;
    }

    public static def main(Rail[String])  {
        new ClockedAtEach_MustFailCompile().execute();
    }
}
