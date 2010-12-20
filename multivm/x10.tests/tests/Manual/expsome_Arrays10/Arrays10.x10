/* Current test harness gets confused by packages, but it would be in package expsome_Arrays10;
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

// file Arrays line 180

class Arrays10TestExp{
  def check(R:Region)  = R.boundingBox();  }

class Hook {
   def run():Boolean = true;
}


public class Arrays10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Arrays10().execute();
    }
}    
