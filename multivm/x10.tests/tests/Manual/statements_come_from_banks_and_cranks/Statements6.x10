/* Current test harness gets confused by packages, but it would be in package statements_come_from_banks_and_cranks;
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

// file Statements line 207
 class LabelledBreakeyBreakyHeart {
 def findy(a:Array[Array[Int](1)](1), v:Int): Boolean {
var found: Boolean = false;
outer: for (var i: Int = 0; i < a.size; i++)
    for (var j: Int = 0; j < a(i).size; j++)
        if (a(i)(j) == v) {
            found = true;
            break outer;
        }
 return found;
}}

class Hook {
   def run():Boolean = true;
}


public class Statements6 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements6().execute();
    }
}    
