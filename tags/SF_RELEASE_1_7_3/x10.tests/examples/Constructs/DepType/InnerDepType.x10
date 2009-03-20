
    /*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that property syntax is accepted.
 *
 * @author vj
 */
public class InnerDepType extends x10Test {
    class Test(i:int) {
       public def this(i:int):Test{self.i==i} { property(i); }
    }
	public def run(): boolean = {
	 
	    var x: Test{self.i==0} = new Test(0); 
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new InnerDepType().execute();
	}
}
