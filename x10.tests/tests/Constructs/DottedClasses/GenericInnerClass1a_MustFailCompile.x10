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

// A simple test case of an inner class which refers to its outer class's 
// generic type parameter.
// Omits the type parameter in a constructor call, which should be caught
// by the compiler.

public class GenericInnerClass1a_MustFailCompile[X] extends x10Test {
  public static def main(Rail[String]){
     val p = new GenericInnerClass1a_MustFailCompile(); // ERR
     p.execute();
  }
 
  class Inner {
    val x : X;
    def this(x:X){this.x=x;}
  }

  public def run():Boolean {
     return true;
  }
} 
