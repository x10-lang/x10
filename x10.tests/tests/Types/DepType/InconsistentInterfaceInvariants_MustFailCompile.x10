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

/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;
import x10.compiler.tests.*; // err markers

/** Tests that invariants due to a super constraint and a sub constraint are 
 * consistent with each other.
 *@author pvarma
 *
 */
public class InconsistentInterfaceInvariants_MustFailCompile extends x10Test { 

    public static interface Test {this.m() == this.l()} {
    public property l():int;
    public property m():int;
     public def put():int;
    }
    
    @ERR public static interface Test1{this.l() == 0n, this.m() == 1n} extends Test { // Semantic Error: Class invariant is inconsistent.
     public def foo(): int;
    }
    
    public def run()=true;
   
	
    public static def main(var args: Rail[String]): void = {
        new InconsistentInterfaceInvariants_MustFailCompile().execute();
    }
   

		
}
