/* Current test harness gets confused by packages, but it would be in package Structs_Converting;
*/

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

// file Structs line 244
class Class2Struct {
  val a : Int;
  def this(a:Int) { this.a = a; }
  def m() = a;
}
struct Struct2Class {
  val a : Int;
  def this(a:Int) { this.a = a; }
  def m() = a;
}

class Hook {
   def run():Boolean = true;
}


public class Structs4 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Structs4().execute();
    }
}    
