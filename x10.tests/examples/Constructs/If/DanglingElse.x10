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

/*
  if (A) if (B) C else D
is interpreted as 
  if (A) {if (B) C else D} 
*/

public class DanglingElse extends x10Test  {
  public static def main(Rail[String]){
		new DanglingElse().execute();
   
  }
  public def run():Boolean {
	  val p:x10Test = new F();
      return p.run();
  }
  class F extends x10Test.BardTest {
  public def test() : void {
	  assert (test2());
  }
  public def test2() : Boolean {
     var passed : Boolean = false;
     if (true) if (true) passed = true; else passed = false;
     if (!passed) return false;
     passed = false;
     if (true) if (false) passed = false; else passed = true;
     if (!passed) return false;
     passed = true;
     if (false) if (false) passed = false; else passed = false;
     if (!passed) return false;
     return true;
  }
  }
}

