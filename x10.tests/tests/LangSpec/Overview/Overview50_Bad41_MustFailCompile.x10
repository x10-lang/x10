/* Current test harness gets confused by packages, but it would be in package Overview_Mat1;
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



public class Overview50_Bad41_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Overview50_Bad41_MustFailCompile().execute();
    }


// file Overview line 195
//OPTIONS: -STATIC_CHECKS
abstract static  class Mat(rows:Long, cols:Long) {
  static type Mat(r:Long, c:Long) = Mat{rows==r&&cols==c};
  public def this(r:Long, c:Long) : Mat(r,c) = {property(r,c);}
  static def makeMat(r:Long,c:Long) : Mat(r,c) = null;
  abstract  operator this + (y:Mat(this.rows,this.cols)):Mat(this.rows, this.cols);
  abstract  operator this * (y:Mat) {this.cols == y.rows} : Mat(this.rows, y.cols);
  static def example(a:Long, b:Long, c:Long) {
    val axb1 : Mat(a,b) = makeMat(a,b);
    val axb2 : Mat(a,b) = makeMat(a,b);
    val bxc  : Mat(b,c) = makeMat(b,c);
    val axc  : Mat(a,c) = (axb1 +axb2) * bxc;
 val wrong1 = axb1 + bxc; // ERR
    //ERROR: val wrong2 = bxc * axb1;
  }

}

 static class Hook {
   def run():Boolean = true;
}

}
