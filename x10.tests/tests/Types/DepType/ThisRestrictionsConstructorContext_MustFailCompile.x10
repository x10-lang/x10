/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

/**
 * Check that the use of this, violating constructor context restrictions fails to compile.
 *
 * @author pvarma
 */


public class ThisRestrictionsConstructorContext_MustFailCompile extends x10Test {
    class Test(i:int, j:int) {
       public def this(i:int, j:int):Test{self.i==i,self.j==j} { property(i,j);}
    }
   public val a: Test = new Test(4n, 4n); // ERR: 'this' and 'super' cannot escape from a constructor or from methods called from a constructor
   public var b: Test;
   
   // this is not allowed in argument deptypes of a constructor
   def this(arg: Test{self == this.a}): ThisRestrictionsConstructorContext_MustFailCompile { // ERR
   	b = arg;
   }
   
   def this(): ThisRestrictionsConstructorContext_MustFailCompile {
   	b = new Test (4n, 4n); // ERR: 'this' and 'super' cannot escape from a constructor or from methods called from a constructor
   }
    
	public def run(): boolean { 
	   return true;
	}
	public static def main(Rail[String]): void {
		new ThisRestrictionsConstructorContext_MustFailCompile().execute();
	}
}
