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

struct Pair[X,Y] {
  val x:X;
  val y:Y;

  static dummy:long = 100;

  public def this(a:X, b:Y) { x = a; y = b; }

  public def toString() = "<"+x+", "+y+">";

  public def first() = x;
  public def second() = y;
  public static def doit(x:long) = dummy + x;
}

public class GenericStructTest extends x10Test {

  public def run(): boolean {
    val s1 = Pair[Long, String](100, "hello");

    chk(s1.toString().equals("<100, hello>"), "toString failure");
    chk(s1.first() == 100);
    chk(Pair.doit(1000) == 1100);

    return true;
  }

  public static def main(Rail[String]) {
    new GenericStructTest().execute();
  }
}
