/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the use of this, violating static context restrictions fails to compile.
 *
 * @author pvarma
 */
public class ThisRestrictionsStaticContext_MustFailCompile extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }
   public final Test a = new Test(4, 4);
   public static Test(:self == this.a) 
   		m(Test(:self == this.a) arg) { 
      return arg;
    }
	public boolean run() { 
	   return true;
	}
	public static void main(String[] args) {
		new ThisRestrictionsStaticContext_MustFailCompile().execute();
	}
}