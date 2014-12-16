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

public class RegionEquality extends x10Test {

    // needs fix for XTENLANG-130

    public def run(): boolean = {
        val size = 10;
        val R: Region{rank==2} = Region.make(0..(size-1), 0..(size-1));
        val S: Region{rank==2} = Region.make(0..(size-1), 0..(size-1));
        return R.equals(S);
    }

    public static def main(var args: Rail[String]): void = {
         new RegionEquality().execute();
    }
}
