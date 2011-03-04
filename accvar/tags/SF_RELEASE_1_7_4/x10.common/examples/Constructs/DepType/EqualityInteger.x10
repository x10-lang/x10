/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**  
* Purpose: Checks boolean comparison type checking is valid.
* @author vcave,11/09/2006
*
*/
public class EqualityInteger extends x10Test {
  
	public boolean run() {
		int(:self==1) un = 1;
		int(:self==2) deux = 2;
		
		return !(un == deux) && (un != deux);
	}

	public static void main(String[] args) {
		new EqualityInteger().execute();
	}
}