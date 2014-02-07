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


public class Comparable1 extends x10Test {
        
    static def test():void {
        val s:Any = "abc";
        val r1 = s instanceof Comparable[String];
        chk(r1);
        val r2 = s instanceof Comparable[Int];
        chk(!r2);
    }

    public def run(): Boolean = {
        test();
        return true;
    }

    public static def main(args: Rail[String]) {
        new Comparable1().execute();
    }

}
