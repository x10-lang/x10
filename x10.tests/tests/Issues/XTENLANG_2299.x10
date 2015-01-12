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
 * @author makinoy 1/2011
 */
class XTENLANG_2299 extends x10Test {

  def test[T](t0:T, t1:T) {
    var t:T = t0;
    finish {
      async t = t1;
    }
    return t;
  }

  public def run(): boolean {
    return test[String]("a", "b").equals("b");
  }
            
  public static def main(Rail[String]) {
    new XTENLANG_2299().execute();
  }
}
