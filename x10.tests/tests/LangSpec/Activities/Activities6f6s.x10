/* Current test harness gets confused by packages, but it would be in package Activities6f6s;
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



public class Activities6f6s extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Activities6f6s().execute();
    }


// file Activities line 307
 static  class Example { static def example() {
var result : Long = 0;
finish {
  async result = 1;
}
assert result == 1;
} }
 static  class Hook{ def run() {Example.example(); return true;}}

}
