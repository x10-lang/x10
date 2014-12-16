/* Current test harness gets confused by packages, but it would be in package Types6e6x;
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

public class Types6e6x extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types6e6x().execute();
    }


// file Classes line 2548
 static interface Named { def name():String; }
 static class NameMap[T]{T <: Named, T haszero} {
   val m = new HashMap[String, T]();
   def put(t:T) { m.put(t.name(), t); }
   def get(s:String):T = m.getOrThrow(s);
}

 static class Hook {
   def run():Boolean = true;
}

}
