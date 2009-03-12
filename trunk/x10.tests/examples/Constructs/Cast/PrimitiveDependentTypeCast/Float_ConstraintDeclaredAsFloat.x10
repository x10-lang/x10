/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Illustrates float dependent type usage
 * Note: Append an 'F' forces constraint representation to be a float.
 * @author vcave
 **/
public class Float_ConstraintDeclaredAsFloat extends x10Test {

	public def run(): boolean = {
		var j: float = 0.00001F;
		// the constraint is represented as a long
		var i: float{self == 0.00002F} = 0.00002F;
		i = (j*2) as float{self == 0.00002F};
		return ((j == 0.00001F) && (i==0.00002F));
	}

	public static def main(var args: Rail[String]): void = {
		new Float_ConstraintDeclaredAsFloat().execute();
	}

}
