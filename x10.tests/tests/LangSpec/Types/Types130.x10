/* Current test harness gets confused by packages, but it would be in package Types_For_Snipes_Interfaces;
*/
// Warning: This file is auto-generated from the TeX source of the language spec.
// If you need it changed, work with the specification writers.


/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;



public class Types130 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types130().execute();
    }


// file Types line 606
 static interface Named {
  def name():String;
}
 static interface Mobile {
  def where():Long;
  def move(howFar:Long):void;
}
 static interface NamedPoint extends Named, Mobile {}
 static class Person implements Named, Mobile {
   var name:String; var pos: Long;
   public def name() = this.name;
   public def move(howFar:Long) { pos += howFar; }
   public def where() = this.pos;
   public def example(putAt:Mobile) {
      this.pos = putAt.where();
   }
}

 static class Hook {
   def run():Boolean = true;
}

}
