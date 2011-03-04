/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Jacobi iteration
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
public class JacobiSmall extends x10Test {

	static final int N = 7;
	static final double epsilon = 0.002;
	static final double epsilon2 = 0.000000001;
	final region(:rank==1) R1 = [0:N+1];
	final region(:rank==1) R2 = [1:N];
	final region(:rank==2) R = [R1,R1];
	final region(:rank==2) R_inner = [R2,R2];
	final dist(:rank==2)  D = (dist(:rank==2)) dist.factory.block(R);
	final dist(:rank==2) D_inner = D | R_inner;
	final dist(:rank==2) D_Boundary = D - D_inner.region;
	static final int EXPECTED_ITERS = 131;
	static final double EXPECTED_ERR = 0.0019977310907846046;

	public boolean run() {

		int iters = 0;

		final double[.] a = new double[D];
		finish ateach (final point p[i,j]: D_inner) { a[p] = (double)(i-1)*N+(j-1); }
		finish ateach (final point p: D_Boundary) { a[p] = (N-1)/2; }
		double err;
		double[.] x = a;
		while (true) {
			final double[.] b = x;
			final double[.] temp = new double[D_inner];
			finish ateach (final point p[i,j]: D_inner) 
				temp[i,j] = (b[i+1,j]+b[i-1,j]+b[i,j-1]+b[i, j+1])/4.0;

			if ((err = (b.restriction(D_inner)
				     .lift(doubleArray.sub, temp)
				     .lift(doubleArray.abs)
				     .reduce(doubleArray.add, 0.0))) < epsilon)
				break;

			x = x.overlay(temp);
			iters++;
		}
		System.out.println(err);
		System.out.println(iters);

		return Math.abs(err-EXPECTED_ERR) < epsilon2 && iters == EXPECTED_ITERS;
	}
	
	public static void main(String[] args) {
		new JacobiSmall().execute();
	}
}

