/* Current test harness gets confused by packages, but it would be in package Activities_Transient_Noir_Orbital_Potatoes;
*/

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

// file Activities line 359
class Trans {
   val a : Int = 1;
   transient val b : Int = 2;
   //ERROR transient val c : Int{c != 0} = 3;
   def example() {
     assert(a == 1 && b == 2);
     at(here) {
        assert(a == 1 && b == 0);
     }
   }
}

class Hook {
   def run():Boolean = true;
}


public class Activities5 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Activities5().execute();
    }
}    
