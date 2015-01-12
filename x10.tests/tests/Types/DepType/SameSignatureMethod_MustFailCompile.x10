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

/** Tests that two methods in a class cannot have the same signature.
 *@author pvarma
 *
 */

import harness.x10Test;

public class SameSignatureMethod_MustFailCompile extends x10Test { 

class Test(i: int, j:int) {  // ERR
		 def tester(k:int(0n))=true; // ERR
		 def tester(l:int(0n)) = l; // ERR ShouldNotBeERR todo: we should get a single error here
		 //Semantic Error: tester(l: x10.lang.Int{self==0}): x10.lang.Int{self==0, l==0} in SameSignatureMethod_MustFailCompile.Test cannot override tester(k: x10.lang.Int{self==0}): x10.lang.Boolean{self==true} in SameSignatureMethod_MustFailCompile.Test; attempting to use incompatible return type.
	 	 //Expected Type: x10.lang.Boolean{self==true}
	 	 //Found Type: x10.lang.Int{self==0, l==0}
	 	 //Semantic Error: Duplicate method "method SameSignatureMethod_MustFailCompile.Test.tester(l:x10.lang.Int{self==0}): x10.lang.Int{self==0, l==0}"; previous declaration at C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Constructs\DepType\SameSignatureMethod_MustFailCompile.x10:29,10-35.]

		 
		def this(i:int, j:int):Test = {
			property(i,j);
		}
	}

	
	public def run(): boolean = {
	   return true;
	}  
	
    public static def main(var args: Rail[String]): void = {
        new SameSignatureMethod_MustFailCompile().execute();
    }
   

		
}
