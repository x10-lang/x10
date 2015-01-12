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
import x10.regionarray.*;

/**
 * The test checks that property syntax is accepted.
 *
 * @author pvarma
 */
public class RailTest extends x10Test {

    public def run(): boolean = {
        val r: Region{rail} = Region.make(0,10);
        var a: Array[double]{rail} = new Array[double](r, (x:Point)=>0.0);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new RailTest().execute();
    }
}
