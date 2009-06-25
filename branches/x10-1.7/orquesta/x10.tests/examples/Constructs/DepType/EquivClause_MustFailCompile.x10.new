/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Checks that decplauses are checked when checking type equality.
 *
 * @author vj
 */
public class EquivClause_MustFailCompile extends x10Test {
    var i: int{self==1} = 1;
    var j: int{self==0} = i;

	public def run(): boolean = {
	   
	    return true;
	}
	public static def main(var args: Rail[String]): void = {
		new EquivClause_MustFailCompile().execute();
	}
	
}
