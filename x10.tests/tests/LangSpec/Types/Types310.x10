/* Current test harness gets confused by packages, but it would be in package Types_cripes_whered_you_get_those_gripes;
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
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;



public class Types310 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types310().execute();
    }


// file Types line 1140
 static  class Matrix(rows:Long, cols:Long){
 public static def someMatrix(): Matrix = null;
 public static def example(){
  val a : Matrix = someMatrix() ;
  var b : Matrix{b.rows == a.cols} ;
}}

 static class Hook {
   def run():Boolean = true;
}

}
