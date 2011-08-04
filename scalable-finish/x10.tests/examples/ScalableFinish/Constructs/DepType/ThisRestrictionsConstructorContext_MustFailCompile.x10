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

 

/**
 * Check that the use of this, violating constructor context restrictions fails to compile.
 *
 * @author pvarma
 */


public class ThisRestrictionsConstructorContext_MustFailCompile   {
    class Test(i:int, j:int) {
       public def this(i:int, j:int):Test{self.i==i,self.j==j} = { property(i,j);}
    }
   public val a: Test = new Test(4, 4);
   public var b: Test;
   
   // this is not allowed in argument deptypes of a constructor
   def this(arg: Test{self == this.a}): ThisRestrictionsConstructorContext_MustFailCompile = {
   	b = arg;
   }
   
   def this(): ThisRestrictionsConstructorContext_MustFailCompile = {
   	b = new Test (4, 4);
   }
    
	public def run(): boolean = { 
	   return true;
	}
	public static def main(Rail[String]): void = {
		new ThisRestrictionsConstructorContext_MustFailCompile().run ();
	}
}