/*
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
	class Test(int i, int j) {
		Test(int i, int j) {
			this.i=i;
			this.j=j;
		}
	}
		
	class Test2(int i) extends Test{
		Test2(int i) {
			super(i,i);
			this.i=i;
		}
	}
	public boolean run() {
		Test2 a = new Test2(1);
	   return true;
	}
	public static void main(String[] args) {
		new SamePropertyAncestor_MustFailCompile().execute();
	}
}