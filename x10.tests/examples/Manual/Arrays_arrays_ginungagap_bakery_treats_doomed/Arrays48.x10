/* Current test harness gets confused by packages, but it would be in package Arrays_arrays_ginungagap_bakery_treats_doomed;
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

// file Arrays line 805
class Example{
def example() {
val A = new Array[Int](1..10, (p:Point(1))=>p(0) );
// A = 1,2,3,4,5,6,7,8,9,10
val cube = (i:Int) => i*i*i;
val B = new Array[Int](A.region); // B = 0,0,0,0,0,0,0,0,0,0
A.map(B, cube);
// B = 1,8,27,64,216,343,512,729,1000
} }

class Hook {
   def run():Boolean = true;
}


public class Arrays48 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Arrays48().execute();
    }
}    
