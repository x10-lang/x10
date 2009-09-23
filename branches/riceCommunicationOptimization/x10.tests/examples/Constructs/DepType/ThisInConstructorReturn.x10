/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the use of this, violating constructor context restrictions 
 fails to compile.
 *
 * @author pvarma
 */


public class ThisInConstructorReturn extends x10Test {
    class Test(R1:Region) {
       val a:Region = [1..10];
       public def this():Test{self.R1==this.a} { 
    	      property(this.a);
       }
    }
    
	public def run(): boolean = { 
		var t: Test! = new Test();
	   return t.R1==t.a;
	}
	public static def main(var args: Rail[String]): void = {
		new ThisInConstructorReturn().execute();
	}
}
