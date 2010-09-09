/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**  
* Purpose: Checks boolean comparison type checking is valid.
* @author vcave,11/09/2006
*
*/
public class EqualityInteger extends x10Test {
  
	public def run(): boolean = {
		var un: int{self==1} = 1;
		var deux: int{self==2} = 2;
		
		return !(un == deux) && (un != deux);
	}

	public static def main(var args: Rail[String]): void = {
		new EqualityInteger().execute();
	}
}
