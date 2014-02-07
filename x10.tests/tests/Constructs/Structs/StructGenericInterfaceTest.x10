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
import x10.regionarray.*;

struct S58[T] implements (Long,Long)=>T {
  private val data:Array[T](2);

  public def this(array:Array[T](2)) {
    data = array;
  }

  public operator this(i:Long, j:Long) = data(i, j);
}

public class StructGenericInterfaceTest extends x10Test {
  public def run(): boolean {
    val r = Region.make(1..5, 1..5);
    val a = new Array[Long](r, (p:Point(2)) => p(0)+p(1));
    val s = S58[Long](a);

    chk(s(1,1) == 2);
    chk(s(4,4) == 8);

    return true;
  }

  public static def main(Rail[String]) {
    new StructGenericInterfaceTest().execute();
  }

}

