/* Current test harness gets confused by packages, but it would be in package Types_Inferred_By_Phone;
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



public class Types510 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types510().execute();
    }


// file Types line 2534
 static class Spot(x:Long) {
  def this() {property(0);}
  def this(xx: Long) { property(xx); }
}
 static class Confirm{
 static val s0 : Spot{x==0} = new Spot();
 static val s1 : Spot{x==1} = new Spot(1);
}

 static class Hook {
   def run():Boolean = true;
}

}
