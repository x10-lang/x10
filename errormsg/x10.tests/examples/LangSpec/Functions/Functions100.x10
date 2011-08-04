/* Current test harness gets confused by packages, but it would be in package expsome_Functions12;
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



public class Functions100 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Functions100().execute();
    }


// file Functions line 388
 static class Span(low:Int, high:Int) {def this(low:Int, high:Int) {property(low,high);} def between(n:Int) = low <= n && n <= high;}
 static class Functions12TestExp{
  def check(digit:Span)  = (n:Int) => digit.between(n);  }

 static class Hook {
   def run():Boolean = true;
}

}