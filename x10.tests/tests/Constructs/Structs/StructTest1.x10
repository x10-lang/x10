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

struct StructTest1__S {
  val x:long;
  val y:long;

  public def this(a:long, b:long) { x = a; y = b; }

  public final def sum() = x + y;

  public final def sum4(o:StructTest1__S) = sum() + o.sum() + o.x;
}

class StructTest1_C {
  val f1: StructTest1__S;
  
  public def this(a:StructTest1__S) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

public class StructTest1 extends x10Test {
  public def run(): boolean {
    val a: StructTest1__S = StructTest1__S(3,4);
    val b: StructTest1__S = StructTest1__S(10,20);
    
    chk(a.sum() == 7, "a.sum() == 7");
    chk(a.sum4(b) == 47, "a.sum4(b) == 47");

    chk(new StructTest1_C(StructTest1__S(100, 50)).sum() == 153);

    chk(test1(a, b) == 40);
    
    return true;
  }

  public static def test1(a:StructTest1__S, b:StructTest1__S) {
    return a.sum() + b.sum() + a.x;
  }

  public static def main(Rail[String]) {
    new StructTest1().execute();
  }

}

