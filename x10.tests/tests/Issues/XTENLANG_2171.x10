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
 * @author bdlucas 12/2008
 */

class XTENLANG_2171 extends x10Test {

  class A implements ITest {
    public def get():int{
      val r:int;
      finish async r = 1n;
        return r;
    }
  }

  public def run(): boolean {
    val a:ITest = new A();
    a.get();
    return true;
  }
            
  public static def main(Rail[String]) {
    new XTENLANG_2171().execute();
  }
}

interface ITest{
    def get():int;
}