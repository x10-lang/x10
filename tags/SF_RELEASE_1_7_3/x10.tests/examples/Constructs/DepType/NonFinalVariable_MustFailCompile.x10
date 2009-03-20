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
public class NonFinalVariable_MustFailCompile extends x10Test {
class Test(i: int) {
	
	   public def this(ii:int):Test = {
	     i = ii;
	   }
	}
	
	
	public def run(): boolean = {
	   var ii: int = 52;
	   var a: Test{i==ii} = new Test(52);
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new NonFinalVariable_MustFailCompile().execute();
	}
}
