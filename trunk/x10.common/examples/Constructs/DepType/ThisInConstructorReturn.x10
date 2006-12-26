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


public class ThisInConstructorReturn extends x10Test {
    class Test(region R1) {
       public final region a = [1:10];
       public Test(:self.R1==this.a)() { 
    	      property(this.a);
       }
    }
    
	public boolean run() { 
		Test t = new Test();
	   return t.R1==t.a;
	}
	public static void main(String[] args) {
		new ThisInConstructorReturn().execute();
	}
}