/* Current test harness gets confused by packages, but it would be in package expsome_Places5;
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

// file Places line 55

class Places5TestExp{
  def check(pl:Place)  = pl.isCUDA();  }

class Hook {
   def run():Boolean = true;
}


public class Places5 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Places5().execute();
    }
}    
