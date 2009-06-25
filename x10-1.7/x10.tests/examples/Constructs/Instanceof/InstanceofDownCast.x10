/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks both inlined and "side effect aware" cast checking code
 *          works correctly
 * Note: This test try instanceof on several local objects and method call returned one.
 *       It also checks that method call (which may have side effects) are only called once per invocation.
 * @author vcave
 **/
public class InstanceofDownCast extends x10Test {
	 // method calls invocation counters
	 private var counter1: int = 0;
	 private var counter2: int = 0;
	 private var counter3: int = 0;
	 
	public def run(): boolean = {
		var upcast1: Object = new X10DepTypeClassOne(1);
		var upcast2: Object = new X10DepTypeSubClassOne(1,2);
		var upcast3: X10DepTypeClassOne = new X10DepTypeSubClassOne(2,3);

		// instance of checked against an object
		var res1: boolean = upcast1 instanceof X10DepTypeClassOne{p==1};
		
		var res2: boolean = upcast2 instanceof X10DepTypeClassOne{p==1};
		var res3: boolean = upcast2 instanceof X10DepTypeSubClassOne{p==1&&a==2};

		var res4: boolean = upcast3 instanceof X10DepTypeClassOne{p==2};
		var res5: boolean = upcast3 instanceof X10DepTypeSubClassOne{p==2&&a==3};

		// instance of checked against a method return
		var res6: boolean = this.getX10DepTypeClassOne(1) instanceof X10DepTypeClassOne{p==1};
		
		var res7: boolean = this.getX10DepTypeSubClassOne(1,2) instanceof X10DepTypeClassOne{p==1};
		var res8: boolean = this.getX10DepTypeSubClassOne(1,2) instanceof X10DepTypeSubClassOne{p==1&&a==2};

		var res9: boolean = this.getX10DepTypeSubClassOne_2(2,3) instanceof X10DepTypeClassOne{p==2};
		var res10: boolean = this.getX10DepTypeSubClassOne_2(2,3) instanceof X10DepTypeSubClassOne{p==2&&a==3};

		
		return (res1 && res2 && res3 && res4 && res5 && 
				res6 && res7 && res8 && res9 && res10 &&
				// checks methods have been called only once per invocation
				// by the dynamic checking code.
				(counter1==1) && (counter2==2) && (counter3==2));
	}
	
	private def getX10DepTypeClassOne(var c1: int): Object = {
		counter1++;
		return new X10DepTypeClassOne(c1);
	}

	private def getX10DepTypeSubClassOne(var c1: int, var c2: int): Object = {
		counter2++;
		return new X10DepTypeSubClassOne(c1, c2);
	}

	private def getX10DepTypeSubClassOne_2(var c1: int, var c2: int): X10DepTypeClassOne = {
		counter3++;
		return new X10DepTypeSubClassOne(c1, c2);
	}

	public static def main(var args: Rail[String]): void = {
		new InstanceofDownCast().execute();
	}

}
