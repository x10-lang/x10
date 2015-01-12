
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

import x10.util.*;
import harness.x10Test;

public class GenericInnerClass3a extends x10Test {
  public static def main(Rail[String]){
     val p = new GenericInnerClass3a();
     p.execute();
  }
  public def run():Boolean {
     val os = new VomOuter[String]("ow");
     val is = os.new VomInner();
     return true;
  }
}

class VomOuter[T] {
   def this(t:T) {}
   class VomInner extends VomOuter[Int] {
      def this() {super(12321n);}
   }
}
