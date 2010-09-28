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
import x10.io.CustomSerialization;

/*
 * Simple test case to check to see if custom serialization
 * protocol is being called by runtime system
 */
public class TestCustomSerialization2 extends x10Test {
  static class CS[X] implements CustomSerialization {
     val x:X;
     val y:X;
     val other:X;
 
     public def serialize():Any = [x,y];
  
     def this(a:Any) {
        val t = a as ValRail[X];
        x = t(0);
        y = t(1);
	other = y;
     }
 
     def this(a:X, b:X) { x = a; y = b; other = x;}
  }

  public def run():boolean {
    val x = new CS[int](10,20);
    chk(!x.x.equals(x.y));
    chk(x.other.equals(x.x));
    at (here.next()) {
        // The custom serialization logic changes other from x to y.
        // Default serialzation would result in other still being equal to x.
        chk(x.other.equals(x.y));
    }
    return true;
  }

  public static def main(Array[String]) {
      new TestCustomSerialization2().execute();
  }

}
  
 