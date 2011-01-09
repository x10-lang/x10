/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * @author makinoy 1/2011
 */
class XTENLANG_2299 extends x10Test {

  def test[T](t0:T) {
    var t:T;
    finish {
      async t = t0;
    }
    return t;
  }

  public def run(): boolean {
    return test[String]("a") == "a";
  }
            
  public static def main(Array[String](1)) {
    new XTENLANG_2299().execute();
  }
}
