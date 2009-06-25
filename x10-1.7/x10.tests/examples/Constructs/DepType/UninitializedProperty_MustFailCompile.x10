/**
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Checks that no property must be left uninitialized by a constructor.
 *
 * @author pvarma
 */
public class UninitializedProperty_MustFailCompile(i:int, j:int) extends x10Test {

	public def this(i:int, j:int):UninitializedProperty_MustFailCompile = {
	    this.i=i; 
	}
	public def run()=true;
	
	public static def main(Rail[String]):void = {
		new UninitializedProperty_MustFailCompile(2,3).execute();
	}
}
