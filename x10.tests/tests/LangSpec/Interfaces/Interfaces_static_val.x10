/* Current test harness gets confused by packages, but it would be in package Interfaces_static_val;
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



public class Interfaces_static_val extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Interfaces_static_val().execute();
    }


// file Interfaces line 18

 static interface Pushable{prio() != 0} {
  def push(): void;
  static val MAX_PRIO = 100;
  abstract static  class Pushedness{}
 static   struct Pushy{}
 static   interface Pushing{}
  static type Shove = Long;
  property text():String;
  property prio():Long;
}
 static class MessageButton(text:String)
  implements Pushable{self.prio()==Pushable.MAX_PRIO} {
  public def push() {
    x10.io.Console.OUT.println(text + " pushed");
  }
  public property text() = text;
  public property prio() = Pushable.MAX_PRIO;
}

 static class Hook {
   def run():Boolean = true;
}

}
