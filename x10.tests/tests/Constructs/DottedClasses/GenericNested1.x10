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


import x10.util.*;
import harness.x10Test;

// A static nested class with all static methods and fields.

public class GenericNested1[T] extends x10Test {
  public static def main(Rail[String]){
     val p = new GenericNested1[Int]();
     p.execute();
  }
  public def run():Boolean {
     return true;   
  }
  static class Nested[U] {
     static def ewe[U](u:U) = u;
     static def tea[T](t:T) = t;
     static def nuu[V]() = new Nested[V]();
     static val gn1 : GenericNested1[Int] = new GenericNested1[Int]();
     static val gn2 : GenericNested1[Nested[Int]] = new GenericNested1[Nested[Int]]();
     static val in1 : Nested[String] = new Nested[String]();
     static val in2 : Nested[GenericNested1[String]] = new Nested[GenericNested1[String]]();
  }
}
