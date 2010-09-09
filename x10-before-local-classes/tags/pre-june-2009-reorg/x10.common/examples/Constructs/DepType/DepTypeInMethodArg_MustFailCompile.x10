/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
public class DepTypeInMethodArg_MustFailCompile extends x10Test {
    class Test(int i, int j) {
       public Test(:self.i==i&&self.j==j)(final int i, final int j){ property(i,j);}
    }
   public boolean m(Test(:i==j) t) { 
      return true;
    }
	public boolean run() {
	// should fail because the declared type of the variable is just Test.
	   Test x =  new Test(1,1); 
	   return m(x);
	}
	public static void main(String[] args) {
		new DepTypeInMethodArg_MustFailCompile().execute();
	}
}
