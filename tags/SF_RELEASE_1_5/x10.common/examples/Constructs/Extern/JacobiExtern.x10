/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Jacobi iteration.
 *
 * At each step of the iteration, replace the value of a cell with
 * the average of its adjacent cells in the (i,j) dimensions.
 * Compute the error at each iteration as the sum of the changes
 * in value across the whole array. Continue the iteration until
 * the error falls below a given bound.
 *
 * @author vj
 * @author cvp
 * @author kemal
 */
public class JacobiExtern extends x10Test {

	static final int N = 6;
	static final double epsilon = 0.002;
	static final double epsilon2 = 0.000000001;
	final region R = [0:(N+1), 0:(N+1)];
	final region R_inner = [1:N, 1:N];
	final dist D = dist.factory.block(R);
	final dist D_inner = D.restriction(R_inner);
	final dist D_Boundary = D.difference(D_inner.region);
	static final int EXPECTED_ITERS = 97;
	static final double EXPECTED_ERR = 0.0018673382039402497;

	static extern double computeError(double[.] dbl, double[.] temp);

	static { System.loadLibrary("JacobiExtern"); }

	public boolean run() {

		int iters = 0;

		final double[.] a = new double[D];
		finish ateach (point p: D_inner) { a[p] = (double)(p[0]-1)*N + (p[1]-1); }
		finish ateach (point p: D_Boundary) { a[p] = (N-1)/2; }
		double err, x10err;
		double[.] x = a;
		while (true) {
			final double[.] b = x;
			final double[.] temp = new double[D_inner];
			finish ateach ( point p: D_inner ) {
				int i = p[0];
				int j = p[1];
				temp[p] = (read(b, i+1, j)+read(b, i-1, j)+read(b, i, j-1)+read(b, i, j+1))/4.0;
			}

			if ((err = computeError(b, temp)) < epsilon)
				break;
			x10err = (b.restriction(D_inner).sub(temp).abs().sum());
			if (x10err != err) {
				System.out.println("Inconsistent errors:"+err+" != "+x10err);
				return false;
			}
			x = x.overlay(temp);

			iters++;
		}
		System.out.println(err);
		System.out.println(iters);
		return Math.abs(err-EXPECTED_ERR)<epsilon2 && iters == EXPECTED_ITERS;
	}

	static double read(final double[.] b, final int i, final int j) {
		return future(b.distribution[i,j]) { b[i,j] }.force();
	}

	public static void main(String[] args) {
		new JacobiExtern().execute();
	}
}

