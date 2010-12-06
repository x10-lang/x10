/* Current test harness gets confused by packages, but it would be in package Activities_Activities_Activities;
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

// file Activities line 716
 class EquivCode {
 static def S(pt:Point) {}
 static def example(D:Dist) {
for (place in D.places()) async at (place) {
    for (p in D|here) {
        S(p);
    }
}
}}

class Hook {
   def run():Boolean = true;
}


public class Activities9 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Activities9().execute();
    }
}    
