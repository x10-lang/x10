/* Current test harness gets confused by packages, but it would be in package Activities_Condato_Example_Not_A_Tree;
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

 import x10.util.*;

public class Activities120 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Activities120().execute();
    }


// file Activities line 532
 static  class Redund[T] {
 val list = new ArrayList[T]();
 var size : Long = 0;
def pop():T {
  var ret : T;
  when(size>0) {
    ret = list.removeAt(0);
    size --;
    }
  return ret;
}
 }

 static class Hook {
   def run():Boolean = true;
}

}
