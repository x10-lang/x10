/* Current test harness gets confused by packages, but it would be in package Classes_Assignments1_oddvec;
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



public class Classes250 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Classes250().execute();
    }


// file Classes line 2184
 static class Oddvec {
  var v : Rail[Long] = new Rail[Long](3);
  public operator this () =
      "(" + v(0) + "," + v(1) + "," + v(2) + ")";
  public operator this () = (newval: Long) {
    for(p in v.range) v(p) = newval;
  }
  public operator this(i:Long) = v(i);
  public operator this(i:Long, j:Long) = [v(i),v(j)];
  public operator this(i:Long) = (newval:Long)
      = {v(i) = newval;}
  public operator this(i:Long, j:Long) = (newval:Long)
      = { v(i) = newval; v(j) = newval+1;}
  public def example() {
    this(1) = 6;   assert this(1) == 6;
    this(1) += 7;  assert this(1) == 13;
  }
 }
 static   class Hook { def run() {
     val a = new Oddvec();
     assert a().equals("(0,0,0)");
     a() = 1;
     assert a().equals("(1,1,1)");
     a(1) = 4;
     assert a().equals("(1,4,1)");
     a(0,2) = 5;
     assert a().equals("(5,4,6)");
     return true;
   }
 }

}
