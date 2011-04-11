/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.util.*;

/** 
 * Test an ambiguity between two meanings of x.f(y).
 @ @author bard
 */

class DisambiguatingMethodAndField extends x10Test {
    public static def main(Array[String](1)) {
        new DisambiguatingMethodAndField().execute();
    }
    
    val a : (Int)=>Int = (Int)=>1;
    def a (Int) = 2;
    
    val b : Array[Int](1) = [11 as Int];
    def b (Int) = 22;
    
    
    public def run() : boolean {
      val t1 = ( this.a(0) == 2 );
      val t2 = ( (this.a)(0) == 1 );
      
      val t3 = ( this.b(0) == 22 );
      val t4 = ( (this.b)(0) == 11 );
      
      chk(t1);
      chk(t2);
      chk(t3);
      chk(t4);
      
      return t1 && t2 && t3 && t4; 
      

    }
   
}
