/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;;

/**
 * Must allow binding components to an existing point.
 *
 * @author igor, 1/2006
 */
public class PointRebinding extends x10Test {

	public def run(): boolean = {
		var p: point = [1, 2];
		var (i, j): point = p;

		return (i == 1 && j == 2);
	}

	public static def main(var args: Rail[String]): void = {
		new PointRebinding().execute();
	}
}
