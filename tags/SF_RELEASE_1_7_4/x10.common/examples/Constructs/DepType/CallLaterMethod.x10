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

 public class CallLaterMethod  extends x10Test { 
  
  public boolean  run() { 
        dist(:rank==2) d = m();
        return true;
    }
    public dist(:rank==2) m() {
    	return [1:10,1:10]->here;
    }
   public static void main(String[] args) {
        new CallLaterMethod().execute();
    }
   
    }