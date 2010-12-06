/* Current test harness gets confused by packages, but it would be in package Activities;
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

// file Activities line 1032
class OneBuffer[T] {
  var datum: T;
  def this(t:T) { this.datum = t; this.filled = true; }
  var filled: Boolean;
  public def send(v: T) {
    when (!filled) {
      this.datum = v;
      this.filled = true;
    }
  }
  public def receive(): T {
    when (filled) {
      v: T = datum;
      filled = false;
      return v;
    }
  }
}

class Hook {
   def run():Boolean = true;
}


public class Activities14 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Activities14().execute();
    }
}    
