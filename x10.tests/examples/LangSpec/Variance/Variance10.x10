/* Current test harness gets confused by packages, but it would be in package Variance_gone;
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
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;



public class Variance10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Variance10().execute();
    }


// file Variance line 93
 static class Get[T] {
  val x: T;
  def this(x: T) { this.x = x; }
  def get(): T = x;
  static def example() {
     val g : Get[Int] = new Get[Int](31);
     val n : Int = g.get();
     x10.io.Console.OUT.print("It's " + n);
     x10.io.Console.OUT.print("It's still " + g.get());
  }
}

 static class Hook {
   def run():Boolean = true;
}

}