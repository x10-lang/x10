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
 * @author bardb 1/2011
 */
public class XTENLANG_2290 extends x10Test {

    public def run(): boolean {
        return true;
    }
    
    public static def main(Rail[String]) {
        new XTENLANG_2290().execute();
    }
}

class Shadow{
  var x : Int;
  def this(x:Int) {
     // Parameter can shadow field
     this.x = x;
  }
  def example(y:Int) {
     val x = "shadow field";
     // ERROR: val y = "shadow param";
     val z = "local";
     for (a in [1,2,3]) {
        // ERROR: val x = "can't shadow";
     }
// LANG-CHANGE-OBSOLETES:      async {
// LANG-CHANGE-OBSOLETES:         val x = "shadow through async";
// LANG-CHANGE-OBSOLETES:      }
// LANG-CHANGE-OBSOLETES:      at (here) {
// LANG-CHANGE-OBSOLETES:         val x = "shadow through at";
// LANG-CHANGE-OBSOLETES:      }
     val f = () => {
       val x = "shadow through closure";
       x
     };
  }
}
