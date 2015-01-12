/* Current test harness gets confused by packages, but it would be in package Interface_field_name_collision_Interfaces7;
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



public class Interface_field_name_collision extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Interface_field_name_collision().execute();
    }


// file Interfaces line 255


 static interface E1 {static val a = 1;}
 static interface E2 {static val a = 2;}
 static interface E3 extends E1, E2{}
 static class Example implements E3 {
  def example() = E1.a + E2.a;
}

 static class Hook {
   def run():Boolean = true;
}

}
