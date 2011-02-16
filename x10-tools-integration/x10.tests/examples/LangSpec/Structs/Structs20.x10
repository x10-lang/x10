/* Current test harness gets confused by packages, but it would be in package Structs20;
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
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;



public class Structs20 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Structs20().execute();
    }


// file Structs line 96
 static struct Pair[T,U](t:T, u:U) {
  def this(t:T, u:U) { property(t,u); }
  def diag(){T==U && t==u} = t;
}
 static  class Hook{ def run() {
   val p = Pair(3,3);
   return p.diag() == 3;
 }}

}