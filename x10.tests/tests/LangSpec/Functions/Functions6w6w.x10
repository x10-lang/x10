/* Current test harness gets confused by packages, but it would be in package Functions6w6w;
*/
// Warning: This file is auto-generated from the TeX source of the language spec.
// If you need it changed, work with the specification writers.


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



public class Functions6w6w extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Functions6w6w().execute();
    }


// file Functions line 128
 static class Funny implements (Boolean) => Long,
                       (Long, Long) => Long
{
  public operator this(Boolean) = 1;
  public operator this(x:Long, y:Long) = 10*x+y;
  static def example() {
    val f <: Funny  = new Funny();
    assert f(true) == 1; // (Boolean)=>Long behavior
    assert f(1,2) == 12; // (Long,Long)=>Long behavior
  }
}
 static  class Hook{ def run() { Funny.example(); return true; }}

}
