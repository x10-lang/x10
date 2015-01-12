/* Current test harness gets confused by packages, but it would be in package Overview_of_Functions_two;
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



public class Overview30 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Overview30().execute();
    }


// file Overview line 136
 static  class Whatevermore {
  def plzchk(){
    val square = (i:Long) => (i*i);
  val r : Rail[Long] = new Rail[Long](10, square);
}}

 static class Hook {
   def run():Boolean = true;
}

}
