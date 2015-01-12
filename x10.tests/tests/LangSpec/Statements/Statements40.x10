/* Current test harness gets confused by packages, but it would be in package Sta_tem_ent_s_expressions;
*/
// Warning: This file is auto-generated from the TeX source of the language spec.
// If you need it changed, work with the specification writers.


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

 import x10.util.*;

public class Statements40 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Statements40().execute();
    }


// file Statements line 268
 static class StmtEx {
  def this() {
     x10.io.Console.OUT.println("New StmtEx made");  }
  static def call() {
     x10.io.Console.OUT.println("call!");}
  def example() {
     var a : Long = 0;
     a = 1; // assignment
     new StmtEx(); // allocation
     call(); // call
  }
}

 static class Hook {
   def run():Boolean = true;
}

}
