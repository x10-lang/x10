/* Current test harness gets confused by packages, but it would be in package Vars_For_Stars;
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



public class Vars70_Bad38_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Vars70_Bad38_MustFailCompile().execute();
    }


// file Vars line 281
abstract static  class VarsForStars{
 abstract def cointoss(): Boolean;
 abstract def println(Any):void;
def check() {
  var muta : Long;
  // ERROR:  println(muta);
  muta = 4;
  val use2A = muta * 10;
  val immu : Long;
 println(immu); // ERR
  if (cointoss())   {immu = 1;}
  else              {immu = use2A;}
  val use2B = immu * 10;
  // ERROR: immu = 5;
}}

 static class Hook {
   def run():Boolean = true;
}

}
