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

/*
This ought to compile just fine.  However, in the Java back end, it doesn't;
as of now, it fails with this:

x10c: ----------    
     1. ERROR in /Users/bard/x10/tests/generics/MultiBound.java (at line 17)    
     	Arithmetic & x10.    
     lang.    
     Arithmetic>void    
     	 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^    
     Duplicate bound Arithmetic    
     ----------    
     1 problem (1 error)

*/

import harness.x10Test;

/**
 * @author bardb 12/2010
 */

class XTENLANG_2188 extends x10Test {

  public def inspect[U](u:U) { U <: Arithmetic[Int] && U <: Arithmetic[Float] } {
    Console.OUT.println("u is " + u.typeName());
  }

  public def run(): boolean {
    return true;
  }

  public static def main(Rail[String]) {
      new XTENLANG_2188().execute();
  }
  

}
