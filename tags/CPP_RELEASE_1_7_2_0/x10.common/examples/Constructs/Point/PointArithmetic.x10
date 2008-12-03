/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing point arithmetic operations.
 *
 * @author igor, 2/2006
 */
public class PointArithmetic extends x10Test {

	const int DIM = 5;

	public boolean run() {
		int sum = 0;
		point p = [2, 2, 2, 2, 2];
		point q = [1, 1, 1, 1, 1];
		int c = 2;

		// First test that the point/point arithmetic works

		point a = p + q;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += a[i];
		System.out.println("p+p: sum = "+sum);
		if (sum != 15) return false;

		point s = p - q;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += s[i];
		System.out.println("p-p: sum = "+sum);
		if (sum != 5) return false;

		point m = p * q;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += m[i];
		System.out.println("p*p: sum = "+sum);
		if (sum != 10) return false;

		point d = p / q;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += d[i];
		System.out.println("p/p: sum = "+sum);
		if (sum != 10) return false;

		// Unary +/-

		point u = -q;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += u[i];
		System.out.println("-p: sum = "+sum);
		if (sum != -5) return false;

		u = +q;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += u[i];
		System.out.println("+p: sum = "+sum);
		if (sum != 5) return false;

		// Then test that the point/constant arithmetic works

		a = p + c;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += a[i];
		System.out.println("p+c: sum = "+sum);
		if (sum != 20) return false;

		s = p - c;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += s[i];
		System.out.println("p-c: sum = "+sum);
		if (sum != 0) return false;

		m = p * c;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += m[i];
		System.out.println("p*c: sum = "+sum);
		if (sum != 20) return false;

		d = p / c;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += d[i];
		System.out.println("p/c: sum = "+sum);
		if (sum != 5) return false;

		a = c + p;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += a[i];
		System.out.println("c+p: sum = "+sum);
		if (sum != 20) return false;

		s = c - p;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += s[i];
		System.out.println("c-p: sum = "+sum);
		if (sum != 0) return false;

		m = c * p;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += m[i];
		System.out.println("c*p: sum = "+sum);
		if (sum != 20) return false;

		d = c / p;
		sum = 0;
		for (int i = 0; i < DIM; i++)
			sum += d[i];
		System.out.println("c/p: sum = "+sum);
		if (sum != 5) return false;

		// Now test that the dimensionality is properly checked

		boolean gotException;
		point r = [1, 2, 3, 4];

		gotException = false;
		sum = 0;
		try {
			a = p + r;
			for (int i = 0; i < DIM; i++)
				sum += a[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			s = p - r;
			for (int i = 0; i < DIM; i++)
				sum += s[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			m = p * r;
			for (int i = 0; i < DIM; i++)
				sum += m[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			d = p / r;
			for (int i = 0; i < DIM; i++)
				sum += d[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			a = r + p;
			for (int i = 0; i < DIM-1; i++)
				sum += a[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			s = r - p;
			for (int i = 0; i < DIM-1; i++)
				sum += s[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			m = r * p;
			for (int i = 0; i < DIM-1; i++)
				sum += m[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		gotException = false;
		sum = 0;
		try {
			d = r / p;
			for (int i = 0; i < DIM-1; i++)
				sum += d[i];
		} catch (RankMismatchException e) {
			gotException = true;
		}
		if (!(sum == 0 && gotException)) return false;

		return true;
	}

	public static void main(String[] args) {
		new PointArithmetic().execute();
	}
}

