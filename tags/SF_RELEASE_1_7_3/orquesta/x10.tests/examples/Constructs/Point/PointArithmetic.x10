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
public class PointArithmetic extends x10Test {

	public const DIM: int = 5;

	public def run(): boolean = {
		var sum: int = 0;
		var p: point = [2, 2, 2, 2, 2];
		var q: point = [1, 1, 1, 1, 1];
		var c: int = 2;

		// First test that the point/point arithmetic works

		var a: point = p + q;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += a(i);
		System.out.println("p+p: sum = "+sum);
		if (sum != 15) return false;

		var s: point = p - q;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += s(i);
		System.out.println("p-p: sum = "+sum);
		if (sum != 5) return false;

		var m: point = p * q;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += m(i);
		System.out.println("p*p: sum = "+sum);
		if (sum != 10) return false;

		var d: point = p / q;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += d(i);
		System.out.println("p/p: sum = "+sum);
		if (sum != 10) return false;

		// Unary +/-

		var u: point = -q;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += u(i);
		System.out.println("-p: sum = "+sum);
		if (sum != -5) return false;

		u = +q;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += u(i);
		System.out.println("+p: sum = "+sum);
		if (sum != 5) return false;

		// Then test that the point/constant arithmetic works

		a = p + c;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += a(i);
		System.out.println("p+c: sum = "+sum);
		if (sum != 20) return false;

		s = p - c;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += s(i);
		System.out.println("p-c: sum = "+sum);
		if (sum != 0) return false;

		m = p * c;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += m(i);
		System.out.println("p*c: sum = "+sum);
		if (sum != 20) return false;

		d = p / c;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += d(i);
		System.out.println("p/c: sum = "+sum);
		if (sum != 5) return false;

		a = c + p;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += a(i);
		System.out.println("c+p: sum = "+sum);
		if (sum != 20) return false;

		s = c - p;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += s(i);
		System.out.println("c-p: sum = "+sum);
		if (sum != 0) return false;

		m = c * p;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += m(i);
		System.out.println("c*p: sum = "+sum);
		if (sum != 20) return false;

		d = c / p;
		sum = 0;
		for (var i: int = 0; i < DIM; i++)
			sum += d(i);
		System.out.println("c/p: sum = "+sum);
		if (sum != 5) return false;

		// Now test that the dimensionality is properly checked

		var gotException: boolean;
		var r: point = [1, 2, 3, 4];

		gotException = false;
		sum = 0;
		try {
			a = p + r;
			for (var i: int = 0; i < DIM; i++)
				sum += a(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			s = p - r;
			for (var i: int = 0; i < DIM; i++)
				sum += s(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			m = p * r;
			for (var i: int = 0; i < DIM; i++)
				sum += m(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			d = p / r;
			for (var i: int = 0; i < DIM; i++)
				sum += d(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			a = r + p;
			for (var i: int = 0; i < DIM-1; i++)
				sum += a(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			s = r - p;
			for (var i: int = 0; i < DIM-1; i++)
				sum += s(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			m = r * p;
			for (var i: int = 0; i < DIM-1; i++)
				sum += m(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			d = r / p;
			for (var i: int = 0; i < DIM-1; i++)
				sum += d(i);
		} catch (var e: RankMismatchException) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new PointArithmetic().execute();
	}
}
