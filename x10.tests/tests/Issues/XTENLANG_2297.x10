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

/**
 * X10 should infer a correct type for f below.
 * It compiles fine (as of now) with the proper type definition,
 * but fails to compile if it is asked to infer the type
 *
 * @author bardb 1/2011
 */
public class XTENLANG_2297 extends x10Test {

    public def run(): boolean {
        return true;
    }
    
    public static def main(Rail[String]) {
        new XTENLANG_2297().execute();
    }
}

class clogua {
  public static def main(argv:Rail[String]) {
    val n = 3n;
    val f 
// : (x:Int){x != n} => Int
          = (x:Int){x != n} => (12n/(n-x));
    Console.OUT.println("f(5)=" + f(5n));    
  }
}
