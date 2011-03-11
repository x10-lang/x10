/* Current test harness gets confused by packages, but it would be in package Statements9x3m;
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



public class Statements9x3m extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Statements9x3m().execute();
    }


// file Statements line 875
 static class Exn extends Throwable{}
 static class SubExn(n:Int) extends Exn{}
 static class Example {
  static def example() {
    var correct : Boolean = false;
    try {
       throw new SubExn(4);
    }
    catch (e : Exn)          { correct = true; }
    catch (e : SubExn)       { assert false; }
    catch (e : Throwable)    { assert false; }
    assert correct;
  }
}
 static  class Hook { def run() { Example.example(); return true; } }

}