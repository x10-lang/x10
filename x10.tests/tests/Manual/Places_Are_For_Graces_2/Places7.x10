/* Current test harness gets confused by packages, but it would be in package Places_Are_For_Graces_2;
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

// file Places line 95
import x10.util.*;
abstract class Thing {}
class DoMine {
  static def dealWith(Thing) {}
  public static def deal(things: List[GlobalRef[Thing]]) {
     for(gr in things) {
        if (gr.home == here) {
           val grHere =
               gr as GlobalRef[Thing]{gr.home == here};
           val thing <: Thing = grHere();
           dealWith(thing);
        }
     }
  }
}

class Hook {
   def run():Boolean = true;
}


public class Places7 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Places7().execute();
    }
}    
