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

// SKIP_NATIVE_X10: XTENLANG-1191 Native X10 does not support non-final generic instance methods

/**
 * @author makinoy 1/2011
 */
class XTENLANG_2301 extends x10Test {

  static def curry[T](f:(T,T)=>T, b:T) = (x:T)=>f(x,b);
  
  public def run(): boolean {
    val inc = curry[Int](((x:Int,y:Int) => x+y), 1n);
    chk(inc(10n) == 11n);
    return true;
  }
  
  class A implements I[String] {
      public def m[U](U){}
  }
  
  interface I[T] {
      def m[U](U):void;
  }
  
  public static def main(Rail[String]) {
    new XTENLANG_2301().execute();
  }
}
