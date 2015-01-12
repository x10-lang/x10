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

interface StructInterfaceGenericTest_Sum {
  def sum():long;
}

struct StructInterfaceGenericTest_S implements StructInterfaceGenericTest_Sum {
  val x:long;
  val y:long;

  public def this(a:long, b:long) { x = a; y = b; }

  public final def sum() = x + y;
}

class StructInterfaceGenericTest_C implements StructInterfaceGenericTest_Sum {
  val f1:StructInterfaceGenericTest_S;
  
  public def this(a:StructInterfaceGenericTest_S) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

class Summer[T]{T<:StructInterfaceGenericTest_Sum} {
   def sum(a:T) = a.sum();
}

public class StructInterfaceGenericTest extends x10Test {
  public def run(): boolean {
    val a = StructInterfaceGenericTest_S(3,4);
    val b = new StructInterfaceGenericTest_C(a);
    val sa = new Summer[StructInterfaceGenericTest_S]();
    val sb = new Summer[StructInterfaceGenericTest_C]();

    chk(sa.sum(a) == 7, "sa.sum(a) == 7");
    chk(sb.sum(b) == 10, "sb.sum(b) == 10");

    return true;
  }

  public static def main(Rail[String]) {
    new StructInterfaceGenericTest().execute();
  }

}

