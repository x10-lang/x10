/* Current test harness gets confused by packages, but it would be in package ClassInnnerclassAnonclassOw;
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
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



public class InnerClasses70 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new InnerClasses70().execute();
    }


// file Classes line 4119
abstract static  class Choice(name: String) {
  def this(name:String) {property(name);}
  def choose(b:Boolean) {
     if (b) this.yes(); else this.no(); }
  abstract def yes():void;
  abstract def no():void;
}

 static class Example {
  static def main(Rail[String]) {
    val n = new Cell[Long](0);
    val c = new Choice("Inc Or Dec") {
      def yes() { n() += 1; }
      def no()  { n() -= 1; }
      };
    c.choose(true);
    Console.OUT.println("n=" + n());
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
