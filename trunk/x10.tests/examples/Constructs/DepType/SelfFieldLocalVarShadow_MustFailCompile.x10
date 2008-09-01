/**
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
public class SelfFieldLocalVarShadow_MustFailCompile extends x10Test {
    class Test(i: int, j:int) {
		
		def this(i:int, j:int):Test = {
			this.i=i;
			this.j=j;
		}
	}

    public def m(var t: Test{i==j}): boolean = { // the type is Test(:self.i==self.j).
      return true;
    }
	public def run(): boolean = {
	    val j: int = 0;
	    var t: Test{self.i==self.j} =  new Test(0,3) as Test{i==j}; // here j goes to the local variable, not self.j
	    // should fail to compile since Test(:self.i==j) is not a subtype of Test(:self.i==self.j)
	    return m(t); 
	}
	public static def main(var args: Rail[String]): void = {
		new SelfFieldLocalVarShadow_MustFailCompile().execute();
	}
}
