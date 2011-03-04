/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Accessing p[3] in a 2D point should cause an array
 * index out of  bounds exception.
 *
 * @author kemal, 6/2005
 */
public class PointIndex extends x10Test {

	public def run(): boolean = {
		var sum: int = 0;
		var gotException: boolean;
		var p: point = [1, 2];

		gotException = false;
		try {
			sum += p(-1);
		} catch (var e: ArrayIndexOutOfBoundsException) {
			gotException = true;
		}
		System.out.println("1: sum = "+sum+" gotException = "+gotException);
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		try {
			sum += p(3);
		} catch (var e: ArrayIndexOutOfBoundsException) {
			gotException = true;
		}
		System.out.println("2: sum = "+sum+" gotException = "+gotException);
		return sum == 0 && gotException;
	}

	public static def main(var args: Rail[String]): void = {
		new PointIndex().execute();
	}
}
