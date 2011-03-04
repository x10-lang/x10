/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests that a constructor invocation satisfies the parameter types and constraint
 * of the constructor declaration.
 *@author pvarma
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
// depclauses in parameter lists of methods and constructors are not currently implemented.
// Work-around: add the depclause to the deptypes of the formal arguments.

import harness.x10Test;

public class ConstructorInvocation extends x10Test { 

    class Test(i:int, j:int) {
	def this(i:int,j:int){i==j}:Test{self.i==self.j} = {
	    property(i,j);
	}
    }
		
    class Test2(k:int) extends Test {
	def this(k:int):Test2{self.k==k} = {
	    super(k,k);
	    property(k);
	}
    }
    public def run() = true;
	
    public static def main(a:Rail[String]):void = {
        new ConstructorInvocation().execute();
    }
   

		
}
