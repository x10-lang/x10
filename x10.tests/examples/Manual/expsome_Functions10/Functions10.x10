/* Current test harness gets confused by packages, but it would be in package expsome_Functions10;
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

// file Functions line 376
class Span(low:Int, high:Int) {def this(low:Int, high:Int) {property(low,high);} def between(n:Int) = low <= n && n <= high;}
class Functions10TestExp{
  def check(digit:Span)  = (n:Int) => digit.between(n);  }

class Hook {
   def run():Boolean = true;
}


public class Functions10 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Functions10().execute();
    }
}    
