/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that two methods in a class cannot have the same signature.
 *@author pvarma
 *
 */

import harness.x10Test;

public class SameSignatureMethod_MustFailCompile extends x10Test { 

class Test(i: int, j:int) {
		 def tester(k:int(0))=true;
		 def tester(l:int(0)) = l;
		 
		def this(i:int, j:int):Test = {
			this.i=i;
			this.j=j;
		}
	}

	
	public def run(): boolean = {
	   return true;
	}  
	
    public static def main(var args: Rail[String]): void = {
        new SameSignatureMethod_MustFailCompile().execute();
    }
   

		
}
