/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a cast involving types which are not related by supertype or subtype
 * relation fails
 *
 * @author pvarma
 */
public class CastChecking_MustFailCompile extends x10Test {
	class Test(int i, int j) {
		Test(:self.i==i && self.j==j)(final int i, final int j) {
			property(i,j);
		}
	}
		
	class Test2(int k) extends Test(:i==j) {
		Test2(:self.i==k&&self.j==k&&self.k==k)(final int k) {
			super(k,k);
			property(k);
		}
	}
	
	class Test3 (int l) {
		Test3(int l) { property(l);}
	}
	public boolean run() {
		Test2(:k==1&&i==j) a =  new Test2(1);
		Test(:i==j) b = a;
		Test d = (Test) new Test3(1);  // must fail compile
	   return true;
	}
	public static void main(String[] args) {
		new CastChecking_MustFailCompile().execute();
	}
}