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

import x10.util.Box;
import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_244 extends x10Test {

    static def check(a:Any, b:Any) = a==b;
    
    public def run():boolean {
        return check(new Box[Int](1n), new Box[Int](1n))==false;
    }

    public static def main(Rail[String]) {
        new XTENLANG_244().execute();
    }
}
