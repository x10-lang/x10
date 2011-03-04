/* Current test harness gets confused by packages, but it would be in package stmtsome_Types3;
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

// file Types.tex,  line 152

class Types3TestStmt{
  def check()  {
    var n : Int{self != 0};
  }}

class Hook {
   def run():Boolean = true;
}


public class Types3 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Types3().execute();
    }
}    
