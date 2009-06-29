/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Cannot bind components of an uninitialized point.
 *
 * @author igor, 1/2006
 */
public class UninitializedPointBinding_MustFailCompile extends x10Test {

	public def run(): boolean = {
                var p(i,j): point;
                p = [1,2];

		return (i == 1 && j == 2);
	}

	public static def main(args: Rail[String]): void = {
		new UninitializedPointBinding_MustFailCompile().execute();
	}
}

