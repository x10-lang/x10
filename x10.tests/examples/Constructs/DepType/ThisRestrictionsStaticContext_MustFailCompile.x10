/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
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
