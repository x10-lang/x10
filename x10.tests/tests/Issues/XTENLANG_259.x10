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
 * @author bdlucas 12/2008
 */

public class XTENLANG_259 extends x10Test implements (int)=>int {

    public operator this(i:int) = 0n;
    public operator this(i:int)=(v:int) {}
        
    public def run() {
        this(0n) = this(0n) + 1n;
        this(0n)++;
        this(0n)--;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_259().execute();
    }
}
