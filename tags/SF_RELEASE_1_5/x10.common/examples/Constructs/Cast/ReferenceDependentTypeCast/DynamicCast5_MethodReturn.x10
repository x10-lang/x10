/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Checks an object returned by a method is correctly 
 * checked against target type constraints.
 * Issue: The objectReturner method should only invoked once by the dynamic cast checking code.
 *        Otherwise if the method has side effects program's behavior may be modified.
 * Note: The following code will use java reflexion to dynamically checks constraints.
 * @author vcave
 **/
public class DynamicCast5_MethodReturn extends x10Test {

	 public int counter = 0;
	 
	public boolean run() {
	
		try {						
			X10DepTypeClassTwo(:p==0&&q==2) convertedObject =
				(X10DepTypeClassTwo(:p==0&&q==2)) this.objectReturner();
			
		}catch(ClassCastException e) {
			return false;
		}
		
		// checks the method has been called only one time.
		return counter == 1;
	}
	
	public x10.lang.Object objectReturner() {
		counter++;
		return new X10DepTypeClassTwo(:p==0&&q==2)(0,2);
	}
	
	public static void main(String[] args) {
		new DynamicCast5_MethodReturn().execute();
	}

}
 