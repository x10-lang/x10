/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Testing for clocked asyncs inside of a clocked ateach.
 * Simplified kernel of the HeatTransfer_v5 program.
 * 
 */
public class ClockedAtEach extends x10Test {

    public def run():boolean {
        clocked finish {
            val D_Base = Dist.makeUnique();
            val P = 4;
            clocked ateach (z in D_Base)  {
//          saying it like this works. Suspect desugaring is wrong.
//          for (z in D_Base) clocked async at (D_Base(z)) {
                for ([q] in 0..P-1) clocked async {
                    next;
                    next;
                }
            }
        }
       return true;
    }

    public static def main(Array[String](1))  {
        new ClockedAtEach().execute();
    }
}
