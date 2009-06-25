/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the variable being defined can be used in the depclause of its type.
 *
 * @author vj
 */
public class VarBeingDefinedInType_MustFailCompile extends x10Test {
    
	public def run(): boolean = {
		var v: int{v ==0} = 0;
		var w: int{w==v} = 1; // cannot reference v in deptype, v is not final.
	    return v==0;
	}
	public static def main(Rail[String]): void = {
		new VarBeingDefinedInType_MustFailCompile().execute();
	}
}
