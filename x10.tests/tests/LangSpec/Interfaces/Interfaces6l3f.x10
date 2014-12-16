/* Current test harness gets confused by packages, but it would be in package Interfaces6l3f;
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



public class Interfaces6l3f extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Interfaces6l3f().execute();
    }


// file Interfaces line 71
 static interface Star { def rise():void; }
 static class AlphaCentauri implements Star {
   public def rise() {}
}
 static class ElvisPresley implements Star {
   public def rise() {}
}
 static class Example {
   static def example() {
      var star : Star;
      star = new AlphaCentauri();
      star.rise();
      star = new ElvisPresley();
      star.rise();
   }
}

 static class Hook {
   def run():Boolean = true;
}

}
