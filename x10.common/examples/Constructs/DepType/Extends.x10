/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a deptype can be extended, and a constructor propagates constraints on super's properties.
 *
 * @author vj
 */
public class Extends extends x10Test {
	class Test(int i, int j) {
		Test(:self.i==i&&self.j==j)(final int i, final int j) {
			property(i,j);
		}
	}
		
	class Test2(int k) extends Test{
		Test2(:self.k==k&&i==k&&j==k)(final int k) {
			super(k,k);
			property(k);
		}
	}
	public boolean run() {
		Test2(:k==1 && i==j) a =  new Test2(1);
		Test(:i==j) b = a;
	   return true;
	}
	public static void main(String[] args) {
		new DepTypeRef().execute();
	}
}