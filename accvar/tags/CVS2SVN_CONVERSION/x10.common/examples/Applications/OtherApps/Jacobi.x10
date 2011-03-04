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
public class Jacobi extends x10Test {

	const int N = 6;
	const double epsilon = 0.002;
	const double epsilon2 = 0.000000001;
	const region(:rank==2) R = [0:N+1, 0:N+1];
	const region(:rank==2) RInner = [1:N, 1:N];
	const dist(:rank==2) D = (dist(:rank==2)) dist.factory.block(R);
	const dist(:rank==2) DInner = D | RInner;
	const dist(:rank==2) DBoundary = D - RInner;
	const int EXPECTED_ITERS = 97;
	const double EXPECTED_ERR = 0.0018673382039402497;

	final double[.] B = new double[D] (point p[i,j])
	{ return DBoundary.contains(p) ? (N-1)/2 : N*(i-1)+(j-1); };

	public boolean run() {
		int iters = 0;
		double err;
		while (true) {
			final double[:distribution==this.DInner] Temp = 
				 new double[DInner] (point [i,j])
			{ return (B[i+1,j]+B[i-1,j]+B[i,j+1]+B[i,j-1])/4.0; };
			if ((err = ((B | this.DInner)-Temp).abs().sum()) < epsilon)break;
			B.update(Temp);
			iters++;
		}
		System.out.println("Error = "+err);
		System.out.println("Iterations = "+iters);
		return Math.abs(err-EXPECTED_ERR) < epsilon2 && iters == EXPECTED_ITERS;
	}

	public static void main(String[] args) {
		new Jacobi().execute();
	}
}

