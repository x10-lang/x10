/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the use of this, violating constructor context restrictions fails to compile.
 *
 * @author pvarma
 */


public class ThisRestrictionsConstructorContext_MustFailCompile extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }
   public final Test a = new Test(4, 4);
   public Test b;
   
   // this is not allowed in argument deptypes of a constructor
   ThisRestrictionsConstructorContext_MustFailCompile(Test(:self == this.a) arg) {
   	b = arg;
   }
   
   ThisRestrictionsConstructorContext_MustFailCompile() {
   	b = new Test (4, 4);
   }
    
	public boolean run() { 
	   return true;
	}
	public static void main(String[] args) {
		new ThisRestrictionsConstructorContext_MustFailCompile().execute();
	}
}