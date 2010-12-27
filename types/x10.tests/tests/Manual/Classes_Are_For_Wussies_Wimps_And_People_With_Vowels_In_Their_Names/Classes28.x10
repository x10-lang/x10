/* Current test harness gets confused by packages, but it would be in package Classes_Are_For_Wussies_Wimps_And_People_With_Vowels_In_Their_Names;
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

// file Classes line 1926
class MyRegion(rank:Int) {
  static type MyRegion(n:Int)=MyRegion{rank==n};
  def this(r:Int):MyRegion(r) {
    property(r);
  }
  def this(diag:Array[Int](1)):MyRegion(diag.size){
    property(diag.size);
  }
  def mockUnion(r:MyRegion(rank)):MyRegion(rank) = this;
  def example() {
    val R1 : MyRegion(3) = new MyRegion([4,4,4]);
    val R2 : MyRegion(3) = new MyRegion([5,4,1]);
    val R3 = R1.mockUnion(R2); // inferred type MyRegion(3)
  }
}

class Hook {
   def run():Boolean = true;
}


public class Classes28 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Classes28().execute();
    }
}    
