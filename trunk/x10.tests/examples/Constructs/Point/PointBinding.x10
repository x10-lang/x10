/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Must allow binding point components.
 *
 * @author igor, 1/2006
 */
public class PointBinding extends x10Test {

	public def run(): boolean = {
		var p(i,j): point = [1, 2];
		return (i == 1 && j == 2);
	}

	public static def main(var args: Rail[String]): void = {
		new PointBinding().execute();
	}
}
