/* Current test harness gets confused by packages, but it would be in package Types_TypeDef_Examples;
*/

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

// file Types line 710
 import x10.util.*;
class TypeExamples {
  static type StringSet = Set[String];
  static type MapToList[K,V] = Map[K,List[V]];
  static type Int(x: Int) = Int{self==x};
  static type Dist(r: Int) = Dist{self.rank==r};
  static type Dist(r: Region) = Dist{self.region==r};
  static type Redund(n:Int, r:Region){r.rank==n}
      = Dist{rank==n && region==r};
}

class Hook {
   def run():Boolean = true;
}


public class Types27 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types27().execute();
    }
}    
