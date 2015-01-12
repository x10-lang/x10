/* Current test harness gets confused by packages, but it would be in package TypeDefs_glip_second;
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

 import x10.util.*;

public class Types240 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types240().execute();
    }


// file Types line 988

 static class Example {
  static type Nonnull[T]{T isref}  = T{self!=null};
  var example : Nonnull[Example] = new Example();
}

 static class Hook {
   def run():Boolean = true;
}

}
