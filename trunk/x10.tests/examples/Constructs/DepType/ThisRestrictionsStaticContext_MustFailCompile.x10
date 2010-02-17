/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Check that the use of this, violating static context restrictions fails to compile.
 *
 * @author pvarma
 */
public class ThisRestrictionsStaticContext_MustFailCompile extends x10Test {
     class Test(i:int, j:int) {
       public def this(i:int, j:int):Test{self.i==i,self.j==j} = { property(i,j);}
    }
   public val a: Test = new Test(4, 4);
   public static def m(var arg: Test{self == this.a}): Test{self == this.a} = { 
      return arg;
    }
	public def run(): boolean = { 
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new ThisRestrictionsStaticContext_MustFailCompile().execute();
	}
}
