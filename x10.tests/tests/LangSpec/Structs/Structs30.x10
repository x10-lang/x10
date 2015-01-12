/* Current test harness gets confused by packages, but it would be in package Structs30Pair;
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



public class Structs30 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Structs30().execute();
    }


// file Structs line 322
 static struct Pair[T,U] {
    public val first:T;
    public val second:U;
    public def this(first:T, second:U):Pair[T,U] {
        this.first = first;
        this.second = second;
    }
    public def toString()
        = "(" + first + ", " + second + ")";
}
 static class Example {
  static def divmod(var a:UInt, b:UInt): Pair[UInt, UInt] {
     assert b > 0u;
     var q : UInt = 0un;
     while (a > b) {q += 1un; a -= b;}
     return Pair(q, a);
  }
  static def example() {
     val qr = divmod(22un, 7un);
     assert qr.first == 3un && qr.second == 1un;
  }
}
 static class Hook{ def run() { Example.example(); return true; } }

}
