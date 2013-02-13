/* Current test harness gets confused by packages, but it would be in package statements_come_from_banks_and_cranks;
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



public class Statements60 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements60().execute();
    }


// file Statements line 386
 static  class LabelledBreakeyBreakyHeart {
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

 static class Hook {
   def run():Boolean = true;
}

}
