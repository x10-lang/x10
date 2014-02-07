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

interface StructInterfaceTest_Sum {
  def sum():long;
}

struct StructInterfaceTest_S implements StructInterfaceTest_Sum {
  val x:long;
  val y:long;

  public def this(a:long, b:long) { x = a; y = b; }

  public final def sum() = x + y;
}

class StructInterfaceTest_C implements StructInterfaceTest_Sum {
  val f1:StructInterfaceTest_S;
  
  public def this(a:StructInterfaceTest_S) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

public class StructInterfaceTest extends x10Test {
  public def run(): boolean {
    val a = StructInterfaceTest_S(3,4);
    val b = new StructInterfaceTest_C(a);
    
    chk(a.sum() == 7, "a.sum() == 7");
    chk(b.sum() == 10, "b.sum() == 10");

    return true;
  }

  public static def main(Rail[String]) {
    new StructInterfaceTest().execute();
  }

}

