/* Current test harness gets confused by packages, but it would be in package Arrays_Some_Examples_Fidget_Fidget;
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

// file Arrays line 73
 class Example {
 static def example() {
val MAX_HEIGHT=20;
val Null = Region.makeUnit();  // Empty 0-dimensional region
val R1 = 1..100; // 1-dim region with extent 1..100
val R2 = (1..100) as Region(1); // same as R1
val R3 = (0..99) * (-1..MAX_HEIGHT);
val R4 = Region.makeUpperTriangular(10);
val R5 = R4 && R3; // intersection of two regions
 } }

class Hook {
   def run():Boolean = true;
}


public class Arrays5 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Arrays5().execute();
    }
}    
