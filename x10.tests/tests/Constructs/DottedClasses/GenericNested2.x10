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

// A static nested class with instance methods, fields, etc.

public class GenericNested2[T] extends x10Test {
  public static def main(Rail[String]){
     val p = new GenericNested2[Int]();
     p.execute();
  } 
  public def run():Boolean {
     return true;   
  }
  static class Nested[U] {
     def ewe[U](u:U) = u;
     def tea[T](t:T) = t;
     def nuu[V]() = new Nested[V]();
     val gn1 : GenericNested2[Int] = new GenericNested2[Int]();
     val gn2 : GenericNested2[Nested[Int]] = new GenericNested2[Nested[Int]]();
     val in1 : Nested[String] = new Nested[String]();
     val in2 : Nested[GenericNested2[String]] = new Nested[GenericNested2[String]]();
  }
}
