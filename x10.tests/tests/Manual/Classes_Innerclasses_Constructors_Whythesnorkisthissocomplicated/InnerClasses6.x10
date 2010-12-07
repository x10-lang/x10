/* Current test harness gets confused by packages, but it would be in package Classes_Innerclasses_Constructors_Whythesnorkisthissocomplicated;
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

// file InnerClasses line 186
class OC {
  class IC {}
  static val oc1 = new OC();
  static val oc2 = new OC();
  static val ic1 = oc1.new IC();
  static val ic2 = oc2.new IC();
}
class Elsewhere{
  def method(oc : OC) {
    val ic = oc.new IC();
  }
}

class Hook {
   def run():Boolean = true;
}


public class InnerClasses6 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new InnerClasses6().execute();
    }
}    
