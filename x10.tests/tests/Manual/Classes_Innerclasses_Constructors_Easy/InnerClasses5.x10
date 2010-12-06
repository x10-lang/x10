/* Current test harness gets confused by packages, but it would be in package Classes_Innerclasses_Constructors_Easy;
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

// file InnerClasses line 167
class OC {
  class IC {}
  def method(){
    val ic = new IC();
  }
}

class Hook {
   def run():Boolean = true;
}


public class InnerClasses5 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new InnerClasses5().execute();
    }
}    
