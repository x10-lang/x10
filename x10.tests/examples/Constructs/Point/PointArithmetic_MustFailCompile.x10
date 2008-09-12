/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing point arithmetic operations.
 *
 * @author igor, 2/2006
 */
public class PointArithmetic_MustFailCompile extends x10Test {

	public const DIM: int = 5;

	public def run(): boolean = {
		var sum: int = 0;
		val p = [2, 2, 2, 2, 2] to point(DIM);
		val q = [1, 1, 1, 1, 1] to point(DIM);
		var c: int = 2;

		// Now test that the dimensionality is properly checked

		var gotException: boolean;
		var r: point = [1, 2, 3, 4] to point;

                var a: point;
                var s: point;
                var m: point;
                var d: point;

                a = p + r;
                s = p - r;
                m = p * r;
                d = p / r;
                a = r + p;
                s = r - p;
                m = r * p;
                d = r / p;

                return true;
        }

	public static def main(var args: Rail[String]): void = {
		new PointArithmetic().execute();
	}
}
