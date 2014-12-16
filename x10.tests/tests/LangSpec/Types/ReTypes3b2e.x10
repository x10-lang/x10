/* Current test harness gets confused by packages, but it would be in package ReTypes3b2e;
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



public class ReTypes3b2e extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new ReTypes3b2e().execute();
    }


// file Types line 158
 static class Pair[X]{
  public val first : X;
  public val second: X;
  public def this(f:X, s:X) {first = f; second = s;}
}

 static class Hook {
   def run():Boolean = true;
}

}
