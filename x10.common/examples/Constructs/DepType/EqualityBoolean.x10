/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**  
* Checks boolean comparison type checking is valid.
* @author vcave,11/09/2006
*
*/
public class EqualityBoolean extends x10Test {
   
	public boolean run() {
		boolean vrai = true;
		boolean faux = false;		

		boolean c1 = (vrai == true);
		boolean c2 = (true == vrai);
		boolean c3 = (faux == false);
		boolean c4 = (false == faux);
		return true;
	}
	
	public static void main(String[] args) {
		new EqualityBoolean().execute();
	}
}