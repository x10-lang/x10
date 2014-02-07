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
 * Test that a deptype for a method arg that depends on a previous arg is handled correctly.
 */
public class DepTypeInMethodArgDependsOnArg extends x10Test {

  public static def arraycopy(val a_dest: Array[double], 
                              val a_src: Array[double]{rank==a_dest.rank}):void { }

    public def run(): boolean = {
        val buffDest: Array[double]{rank==2} = new Array[double](Region.make(1..10, 1..10));
        val buffSrc = buffDest;
        arraycopy(buffDest,  buffSrc);
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new DepTypeInMethodArgDependsOnArg().execute();
    }
}
