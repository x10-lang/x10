/* Current test harness gets confused by packages, but it would be in package Types6s1l;
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



public class Types6s1l extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types6s1l().execute();
    }


// file Types line 1416
//OPTIONS: -STATIC_CHECKS
 static class Keyed {
  private val k : Long;
  public def this(k : Long) {
    this.k = k;
  }
  public def secret(q:Long){q==this.k} = 11;
  public def key():Long{self==this.k} = this.k;
}
 static class Snooper {
  public static def main(argv:Rail[String]) {
    val keyed : Keyed = new Keyed(8);
    //ERROR: keyed.secret(keyed.k);
    //ERROR: keyed.secret(8);
    val kk = keyed.key();
    keyed.secret(kk);
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
