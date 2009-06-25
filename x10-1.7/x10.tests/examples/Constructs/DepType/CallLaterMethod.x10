/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 /**  Check that the return type of a call to a method which has a deptype as its return
 type is handled correctly. 
 
 *@author vj,10/20/2006
 *
 */

import harness.x10Test;

 public class CallLaterMethod extends x10Test { 
  
  public def run(): boolean = { 
        var d: Dist{rank==2} = m();
        return true;
    }
    public def m(): Dist{rank==2} = {
        val r:Region(2) = Region.makeRectangular([1,1], [10,10]);
    	return Dist.makeConstant(r, here);
    }
   public static def main(var args: Rail[String]): void = {
        new CallLaterMethod().execute();
    }
   
    }
