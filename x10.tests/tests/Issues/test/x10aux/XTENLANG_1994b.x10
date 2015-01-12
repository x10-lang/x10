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

package test.x10aux;

import harness.x10Test;
import test.x10.XTENLANG_1994;

/**
 * Basic test to see if it is possible to use
 * x10 as part of a package name and not clash 
 * with the x10 package.
 */
public class XTENLANG_1994b extends x10Test {

    public def run(): boolean {
        val other = new XTENLANG_1994().run();
        val x = MyStruct(10,20).sum();
        val y = new MyObj(2,4).sum();
        return x+y == 36 && other;
    }

    public static def main(Rail[String]) {
        new XTENLANG_1994b().execute();
    }
}

interface Summer {
  def sum():long;
}

struct MyStruct implements Summer {
  val a:long;
  val b:long;
 
  public def this(x:long, y:long) { a = x; b = y; }
  
  public def sum() = a + b;
}

class MyObj implements Summer {
  val a:long;
  val b:long;
 
  public def this(x:long, y:long) { a = x; b = y; }
  
  public def sum() = a + b;
}


