/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that a nullable type can have a  dep clause.
 *@author vj
 *
 */

import harness.x10Test;

public class DepClauseForNullableType extends x10Test { 
    class Test(int k) { 
        Test(int kk) {
            super();
	    property(kk);
        }
    }
    
    nullable < Test> (:self == null) n;
    
    
    public  boolean run() {
	
	return true;
    }
	
    public static void main(String[] args) {
        new DepClauseForNullableType().execute();
    }
   

		
}
