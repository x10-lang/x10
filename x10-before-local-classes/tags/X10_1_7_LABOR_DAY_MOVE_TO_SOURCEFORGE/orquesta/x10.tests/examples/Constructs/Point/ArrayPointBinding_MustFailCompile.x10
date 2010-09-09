/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Cannot bind point components in array declaration.
 *
 * @author igor, 1/2006
 */
public class ArrayPointBinding_MustFailCompile extends x10Test {

	public def run(): boolean = {
		p(i,j): Rail[point] = new Rail[point](1);
		p(0) = [1,2];

		return (i == 1 && j == 2);
	}

	public static def main(args: Rail[String]): void = {
		new ArrayPointBinding_MustFailCompile().execute();
	}
}

