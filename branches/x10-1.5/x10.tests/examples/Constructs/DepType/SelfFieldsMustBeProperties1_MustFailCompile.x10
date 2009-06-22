/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that only properties can be accessed via self.
 *
 * @author vj
 */
public class SelfFieldsMustBeProperties1_MustFailCompile extends x10Test {

class Test(i:int) {
	   public val bad:boolean; // not declared as a property.
	   public def this(ii:int):Test {
	     property(ii);
	     bad = true;
	   }
	}
	
	
	public def run(): boolean = {
	   var a: Test{i==52} = new Test(52) as Test{i==52 && self.bad};
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new SelfFieldsMustBeProperties1_MustFailCompile().execute();
	}
}
