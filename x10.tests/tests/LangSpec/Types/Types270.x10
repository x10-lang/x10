/* Current test harness gets confused by packages, but it would be in package Types_TypeDef_Examples;
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

import x10.util.*;
import x10.regionarray.*;

public class Types270 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types270().execute();
    }


// file Types line 1023

 static class TypeExamples {
  static type StringSet = Set[String];
  static type MapToList[K,V] = Map[K,List[V]];
  static type Long(x: Long) = Long{self==x};
  static type Dist(r: Long) = Dist{self.rank==r};
  static type Dist(r: Region) = Dist{self.region==r};
  static type Redund(n:Long, r:Region){r.rank==n}
      = Dist{rank==n && region==r};
}

 static class Hook {
   def run():Boolean = true;
}

}
