/*
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
	class Test(int i) {
	   public final boolean bad; // not declared as a property.
	   public Test(int ii) {
	     property(ii);
	     bad = true;
	   }
	}
	
	public boolean run() {
	   Test (:i==52) a = (Test(:i==52 && bad==true)) new Test(52);
	    return true;
	}
	public static void main(String[] args) {
		new SelfFieldsMustBeProperties_MustFailCompile().execute();
	}
}

