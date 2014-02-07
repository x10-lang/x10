/* Current test harness gets confused by packages, but it would be in package Types6s1l_Bad41_MustFailCompile;
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

// file Types line 1416
//OPTIONS: -STATIC_CHECKS
class Keyed {
  private val k : Long;
  public def this(k : Long) {
    this.k = k;
  }
  public def secret(q:Long){q==this.k} = 11;
  public def key():Long{self==this.k} = this.k;
}

class Snooper {
  public static def main(argv:Rail[String]) {
    val keyed : Keyed = new Keyed(8);
 keyed.secret(keyed.k); // ERR
    //ERROR: keyed.secret(8);
    val kk = keyed.key();
    keyed.secret(kk);
  }
}


public class Types6s1l_Bad41_MustFailCompile extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types6s1l_Bad41_MustFailCompile().execute();
    }

 static class Hook {
   def run():Boolean = true;
}

}
