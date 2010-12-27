/* Current test harness gets confused by packages, but it would be in package Arrays_DistArray_Construction_FeralWolf;
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

// file Arrays line 624
 class Example {
 def example() {
val data : DistArray[Int]
    = DistArray.make[Int](1..1000->here, ([i]:Point(1)) => i);
val blocked = Dist.makeBlock((1..1000)*(1..1000));
val data2 : DistArray[Int]
    = DistArray.make[Int](blocked, ([i,j]:Point(2)) => i*j);
 }  }

class Hook {
   def run():Boolean = true;
}


public class Arrays38 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Arrays38().execute();
    }
}    
