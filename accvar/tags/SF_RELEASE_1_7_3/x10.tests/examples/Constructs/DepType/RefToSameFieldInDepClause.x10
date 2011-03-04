/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ref to same field in a dependent clause.
 *
 * @author pvarma
 */

public class RefToSameFieldInDepClause extends x10Test { 

    var v: int{v == 0};
	
    public def run(): boolean = {
	
	return true;
    }
	
    public static def main(var args: Rail[String]): void = {
        new RefToSameFieldInDepClause().execute();
    }
   

		
}
