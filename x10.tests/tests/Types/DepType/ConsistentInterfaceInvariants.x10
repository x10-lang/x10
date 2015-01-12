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

/** Tests that invariants due to a super constraint and a sub constraint are 
 * consistent with each other.
 *@author pvarma
 *
 */

import harness.x10Test;

public class ConsistentInterfaceInvariants extends x10Test { 

    public static interface Test {m() == l()} {
    public property l():int;
    public property m():int;
	public def put():int;
    }
    
    public static interface Test1 {l() == n() && m() == n()} extends Test {
    public property n():int;
	public def foo():int;
    }
    
    public def run():boolean=true;
	
    public static def main(a: Rail[String]) = {
        new ConsistentInterfaceInvariants().execute();
    }
   

		
}
