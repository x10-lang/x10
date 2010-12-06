/* Current test harness gets confused by packages, but it would be in package Arrays_Arrays_mostly_dire_dreams_tonight;
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

// file Arrays line 362
class Example{
def example(A:Array[Int]) {
for (p in A) A(p) = 2*A(p);
}}

class Hook {
   def run():Boolean = true;
}


public class Arrays26 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Arrays26().execute();
    }
}    
