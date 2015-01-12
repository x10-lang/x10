/* Current test harness gets confused by packages, but it would be in package Extern_or_turnabout_is_fair_play;
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

import x10.compiler.Native;

public class extern50 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new extern50().execute();
    }


// file NativeCode line 116
 static class Species {
  @Native("c++","printf(\"Sum=%d\", ((#1)+(#2)) )")
  @Native("java","System.out.println(\"\" + ((#1)+(#2)))")
  static native def printNatively(x:Int, y:Int):void;
}

 static class Hook {
   def run():Boolean = true;
}

}
