/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method arg can have a deptype and it is propagated into the body.
 *
 * @author vj
 */
public class MethodArgDepTypes extends x10Test {
   class Test(int i, int j) {
     public Test(int ii, int jj) { i=ii; j=jj;}
   }
   public boolean m(Test(:i==j) t) { 
      Test(:i==j) tt = t;
      return true;
    }
	public boolean run() {
	   return m((Test(:i==j)) new Test(2,2));
	}
	public static void main(String[] args) {
		new MethodArgDepTypes().execute();
	}
}