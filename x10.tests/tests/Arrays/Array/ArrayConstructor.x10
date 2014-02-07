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
 * @author vj
 */

public class ArrayConstructor extends x10Test {

    public def run(): boolean = {
        val x  = 1.0;
        val a  = new Array[double](6, 2.0);
        return true;
    }

    public static def main(Rail[String]) {
        new ArrayConstructor().execute();
    }
}
