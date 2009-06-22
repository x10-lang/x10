/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Check that an int it generates the correct depclause for its type.
 */
public class IntLitDepType_MustFailCompile extends x10Test {
	public def run(): boolean = {
		var f: int{self==1} = 2;
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntLitDepType_MustFailCompile().execute();
	}


}
