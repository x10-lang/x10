/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that an argument of type Test(:self.i==j), where j is a local variable, 
   cannot be passed as an argument to a method which requires Test(:self.i==self.j).
 *
 * @author vj
 */
public class SelfFieldLocalVarShadowMustFailCompile extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }

    public boolean m(Test(:i==j) t) { // the type is Test(:self.i==self.j).
      return true;
    }
	public boolean run() {
	    final int j = 0;
	    Test(:i==j) t = (Test(:i==j)) new Test(0,3); // here j goes to the local variable, not self.j
	    // should fail to compile since Test(:self.i==j) is not a subtype of Test(:self.i==self.j)
	    return m(t); 
	}
	public static void main(String[] args) {
		new SelfFieldLocalVarShadowMustFailCompile().execute();
	}
}
