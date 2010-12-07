/* Current test harness gets confused by packages, but it would be in package Types_Structs_Coords;
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

// file Types line 311
struct Position {
  public val x:Double, y:Double, z:Double;
  def this(x:Double, y:Double, z:Double) {
     this.x = x; this.y = y; this.z = z;
  }
}

class Hook {
   def run():Boolean = true;
}


public class Types12 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types12().execute();
    }
}    
