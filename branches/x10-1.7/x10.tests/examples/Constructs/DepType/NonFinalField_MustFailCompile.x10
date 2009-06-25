/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that field accesses within a depclause are checked to be final.
 *
 * @author vj
 */
public class NonFinalField_MustFailCompile extends x10Test {
   public var bad:boolean=true;
	class Test(i: int) {
	
	   public def this(ii:int):Test = {
	     i = ii;
	   }
	}
	
	public def run(): boolean = {
	   var a: Test =  new Test(52) as Test{i==52, bad};
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new NonFinalField_MustFailCompile().execute();
	}
}
