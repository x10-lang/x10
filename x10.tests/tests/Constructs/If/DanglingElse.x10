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

/*
  if (A) if (B) C else D
is interpreted as 
  if (A) {if (B) C else D} 
*/

public class DanglingElse extends x10Test  {
  public static def main(Rail[String]){
      new DanglingElse().execute();
  }

  def t():Boolean = true;
  def f():Boolean = false;

  public def run():Boolean {
     var passed : Boolean = false;
     if (t()) if (t()) passed = true; else passed = false;
     if (!passed) return false;
     passed = false;
     if (t()) if (f()) passed = false; else passed = true;
     if (!passed) return false;
     passed = true;
     if (f()) if (t()) passed = false; else passed = false;
     if (!passed) return false;
     return true;
  }
}

