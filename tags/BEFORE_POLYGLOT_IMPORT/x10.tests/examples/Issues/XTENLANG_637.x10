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
 * @author hhorii 02/2010
 */

class XTENLANG_637 extends x10Test {

    public def run(): boolean {
        val sub = new Sub();
        val csub = new Get[Sub](sub);
        val csup1 : Get[Super]! = csub as Get[Super]!;
        val csup2 : Get[Super]! = csub;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_637().execute();
    }
}

class Get[+X] {
  var x : X;
  def this(x:X) { this.x = x; }
}        
class Super  {
}

class Sub extends Super {
}