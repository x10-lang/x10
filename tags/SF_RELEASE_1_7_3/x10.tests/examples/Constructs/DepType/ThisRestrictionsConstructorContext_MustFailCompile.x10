/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the use of this, violating constructor context restrictions fails to compile.
 *
 * @author pvarma
 */


public class ThisRestrictionsConstructorContext_MustFailCompile extends x10Test {
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
		new ThisRestrictionsConstructorContext_MustFailCompile().execute();
	}
}
