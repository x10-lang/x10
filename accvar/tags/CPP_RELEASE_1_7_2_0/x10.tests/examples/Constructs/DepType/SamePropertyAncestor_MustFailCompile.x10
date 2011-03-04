/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a class with a property P p cannot have an ancestor 
 * with a property named p (no property name overriding) 
 *
 * @author pvarma
 */
public class SamePropertyAncestor_MustFailCompile extends x10Test {
	class Test(i: int, j:int) {
		 
		def this(i:int, j:int):Test = {
			this.i=i;
			this.j=j;
		}
	}
	
	class Test2(i: int) {
		 
		def this(i:int):Test2 = {
		    super(i,i);
			this.i=i;
		}
	}
		
	
	public def run(): boolean = {
		val a = new Test2(1);
	   return true;
	}
	public static def main(var args: Rail[String]): void = {
		new SamePropertyAncestor_MustFailCompile().execute();
	}
}
