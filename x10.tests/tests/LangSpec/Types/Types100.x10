/* Current test harness gets confused by packages, but it would be in package Types_By_Cripes_Classes;
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
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;



public class Types100 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types100().execute();
    }


// file Types line 520
 static interface Poser {
  def pos():Long;
  }
 static class Position implements Poser {
  private var x : Long = 0;
  public def move(dx:Long) { x += dx; }
  public def pos() : Long = x;
  static def example() {
    var p : Position;
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
