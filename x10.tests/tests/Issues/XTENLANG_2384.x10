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

// SKIP_NATIVE_X10: XTENLANG-2384: Catch with constrained type is a future feature
// SKIP_MANAGED_X10: XTENLANG-2384: Catch with constrained type is a future feature


/**
 * @author makinoy 1/2011
 */
class XTENLANG_2384 extends x10Test {

  static class Exn extends Exception {}
  static class SubExn(n:Int) extends Exn{}
  static class Example {
  static def example() {
    var correct : Boolean = false;
    try {
       throw new SubExn(4n);
    }
    catch (e : SubExn{n==1n}) { assert false; }
    catch (e : SubExn{n==2n}) { assert false; }
    catch (e : Exn)          { correct = true; }
    catch (e : Exception)    { assert false; }
    assert correct;
  }
}
  
  public def run(): boolean {
     Example.example();
     return true;
  }
  
  
  public static def main(Rail[String]) {
    new XTENLANG_2384().execute();
  }
}
