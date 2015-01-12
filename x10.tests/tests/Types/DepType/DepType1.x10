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

/** Tests many syntactic features of dep types.
 *@author pvarma
 *
 */

import harness.x10Test;

public class DepType1(i:int, j:int) extends x10Test { 
    val v: int(0n);
    val b: boolean(true);
    
     //  property declaration for an inner class.
    class Test(k:int) extends DepType1 { 
        def this(k:int):Test{self.k==k} = {
            super(k,k);
	        property(k);
        }
    }
    var t:Test;
   
    
    public def this(i:int, j:int):DepType1{self.i==i,self.j==j} = {
      property(i,j);
      v=0n;
      b=true;
    }
    
 
    public def run():boolean= {
    d:DepType1{self.i==3n}= new DepType1(3n,6n);
	return true;
    }
	
    public static def main(a: Rail[String]):void = {
        new DepType1(3n,9n).execute();
    }
   

		
}
