/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2009-2010.
 */

import harness.x10Test;
import x10.util.*;

/** 
 * Test an ambiguity between two meanings of x.f(y).
 @ @author bard
 */

class DisambiguatingMethodAndField extends x10Test {
    public static def main(Rail[String]) {
        new DisambiguatingMethodAndField().execute();
    }
    
    val a : (Int)=>Int = (Int)=>1n;
    def a (Int) = 2n;
    
    val b : Rail[Int] = [11n as Int];
    def b (Int) = 22n;
    
    
    public def run() : boolean {
      val t1 = ( this.a(0n) == 2n );
      val t2 = ( (this.a)(0n) == 1n );
      
      val t3 = ( this.b(0n) == 22n );
      val t4 = ( (this.b)(0n) == 11n );
      
      chk(t1);
      chk(t2);
      chk(t3);
      chk(t4);
      
      return t1 && t2 && t3 && t4; 
      

    }
   
}
