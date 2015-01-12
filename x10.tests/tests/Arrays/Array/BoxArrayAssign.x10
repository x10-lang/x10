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

import x10.util.Box;
import x10.regionarray.*;

import harness.x10Test;

/**
 * 
 * 
 * @author shane
 * @author vj
 */

public class BoxArrayAssign extends x10Test {

    public def run(): boolean = {
        val table = new Array[Box[Complex]](Region.make(1, 5), (Point)=>(null as Box[Complex]));
        for (val p: Point(1) in table) async table(p) = null;
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new BoxArrayAssign().execute();
    }

}
