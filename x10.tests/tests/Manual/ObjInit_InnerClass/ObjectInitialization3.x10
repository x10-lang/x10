/* Current test harness gets confused by packages, but it would be in package ObjInit_InnerClass;
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

// file ObjectInitialization line 289
class Outer {
  static val leak : Cell[Outer] = new Cell[Outer](null);
  class Inner {
     def this() {Outer.leak.set(Outer.this);}
  }
  def /*Outer*/this() {
     //ILLEGAL: val inner = this.new Inner();
  }
}

class Hook {
   def run():Boolean = true;
}


public class ObjectInitialization3 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new ObjectInitialization3().execute();
    }
}    
