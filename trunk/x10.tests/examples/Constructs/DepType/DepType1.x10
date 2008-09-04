/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests many syntactic features of dep types.
 *@author pvarma
 *
 */

import harness.x10Test;

public class DepType1(i:int, j:int) extends x10Test { 
    val v: int(0);
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
      v=0;
      b=true;
    }
    
 
    public def run():boolean= {
    d:DepType1{self.i==3}= new DepType1(3,6);
	return true;
    }
	
    public static def main(a: Rail[String]):void = {
        new DepType1(3,9).execute();
    }
   

		
}
