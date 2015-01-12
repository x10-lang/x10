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
 * @author bdlucas 10/2008
 */

class XTENLANG_109 extends x10Test {

    static class R(zb:boolean) {
    
        def this(zb:boolean) = property(zb);
    
        static def m(min:int, max:int): R{self.zb==(min==0n)} { throw new Exception(); } // ERR
    
        static def make(min:int, max:int): R{self.zb==(min==0n)} { // ERR
            return m(min, max);
            //return m(min, max) as R{zb==(min==0n)}; // this doesn't work either
        }
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_109().execute();
    }
}
