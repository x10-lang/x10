/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that a depclause cannot reference a field of the type
 in which it occurs (even if the field is final), unless the field is 
 declared as a property. (Such a field access is to be considered 
		 implicitly qualified with self.)
 *
 * @author vj
 */
public class SelfFieldsMustBeProperties_MustFailCompile extends x10Test {
	class Test(i:int) {
	   public val bad:boolean; // not declared as a property.
	   public def this(ii:int):Test {
	     property(ii);
	     bad = true;
	   }
	}
	
	public def run(): boolean = {
	   var a: Test{i==52} = new Test(52) as Test{i==52 && bad} ;
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new SelfFieldsMustBeProperties_MustFailCompile().execute();
	}
}
