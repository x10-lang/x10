/* Current test harness gets confused by packages, but it would be in package Types9j6e;
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



public class Types9j6e extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Types9j6e().execute();
    }


// file Types line 1834
 // SKIP_NATIVE_X10: XTENLANG-2916 Implementation limitation
 // SKIP_MANAGED_X10: XTENLANG-2916 Implementation limitation
 static class Generic {
  public static def inst[T](x:Any):Boolean = x instanceof T;
  // With -VERBOSE, the following line gets a warning
  public static def cast[T](x:Any):T       = x as T;
}
 static class Pea(p:Long) {}
 static class Example{
  static def example() {
     val pea : Pea = new Pea(1);
     // These are what you'd expect:
     assert (pea instanceof Pea{p==1});
     assert (pea as Pea{p==1}).p == 1;
     assert ! (pea instanceof Pea{p==2});
     // 'val x = pea as Pea{p==2};'
     // throws a FailedDynamicCheckException.

     // But the genericized versions don't do the same thing:
     assert Generic.inst[Pea{p==1}](pea);
     assert Generic.inst[Pea{p==2}](pea);
     // No exception here!
     val cast1: Pea{p==1} = Generic.cast[Pea{p==1}](pea);
     val cast2: Pea{p==2} = Generic.cast[Pea{p==2}](pea);
     assert cast2.p == 1;
     assert !(cast2 instanceof Pea{p==2});
  }
}

 static class Hook{ def run() {Example.example(); return true;}}

}
