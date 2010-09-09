/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import java.util.Random;
import harness.x10Test;

/**
 * Version of the JGF SparseMatmult benchmark with
 * mostly values and no async operations (differs in
 * terms of how data is distributed from JGF).
 *
 * This version also uses the Random number generator
 * differently and does not iterate, hence the different
 * final result.
 *
 * @author Christian Grothoff
 */
public class SparseMatmultAllValuesClean extends x10Test {

	static final long RANDOM_SEED = 10101010;
	static final int size = 0;
	static final int[] datasizes_M = { 500, 100000, 500000 };
	static final int[] datasizes_N = { 500, 100000, 500000 };
	static final int[] datasizes_nz = { 2500, 500000, 2500000 };
	static final Random R = new Random(RANDOM_SEED);

	public SparseMatmultAllValuesClean() { }

	public static void main(String[] args) {
		new SparseMatmultAllValuesClean().execute();
	}

	int pos; // X10 hack (local variable of run that must be modified in inner class)

	public boolean run() {
		final int nthreads  = place.MAX_PLACES;
		final dist d_places = dist.factory.unique(place.places);
		final region r_N  = [0 : datasizes_N[size]-1];
		final region r_M  = [0 : datasizes_M[size]-1];
		final region r_nz = [0 : datasizes_nz[size]-1];
		final region r_nthreads = [0 : nthreads-1];
		final dist d_N = dist.factory.block(r_N, place.places);
		final dist d_M = dist.factory.block(r_M, place.places);
		final dist d_nz = dist.factory.block(r_nz, place.places);
		final dist d_nthreads = dist.factory.unique(place.places);

		final int[.] row
			= new int[r_nz->here] (point[i]) {
				return Math.abs(R.nextInt()) % datasizes_M[size];
			};
		final int[.] col
			= new int[r_nz->here] (point[i]) {
				return Math.abs(R.nextInt()) % datasizes_N[size];
			};

		// val is the representation of the sparse MxN matrix;
		// val[i,j] corresponds to matrix[row[i],col[j]]
		final double [.] val
			= new double[r_nz->here] (point[i]) {
				return R.nextDouble();
			};

		// reorder arrays for parallel decomposition
		// reorders the matrix to group entries that will
		// be processed at the same place.

		final int[.] rowt = new int[r_nz->here];
		final int[.] colt = new int[r_nz->here];
		final double[.] valt = new double[r_nz->here];

		pos = 0;
		final region value[.] workranges
			= new region value [r_nthreads->here] (point[j]) {
				region yrange = (d_M | d_places[j]).region;
				int low = pos;
				int high = low-1;
				for (int i = 0; i < datasizes_nz[size]; i++) {
					if (! yrange.contains(point.factory.point(row[i])))
						continue;
					high++;
					rowt[high] = row[i];
					colt[high] = col[i];
					valt[high] = val[i];
				}
				pos = high + 1;
				return [low:high];
			};
		//assert (pos == datasizes_nz[size]);

		if (pos != datasizes_nz[size]) throw new Error();

		// convert to values
		final int value[.] Vrow
			= new int value [r_nz->here] (point[i]) {
				return rowt[i];
			};
		final int value[.] Vcol
			= new int value [r_nz->here] (point[i]) {
				return colt[i];
			};
		final double value [.] Vval
			= new double value [r_nz->here] (point[i]) {
				return valt[i];
			};

		// "x" value with which matrix is going to be multiplied
		final double value[.] x = RandomVector(d_N);

		// "y" is the vector with which is supposed to become y : = mat(val) * x
		final double[.] y = new double[d_M];

		// do parallel multiplication
		finish ateach (final point p[i] : d_places)
			mul(Vval, x, y, Vrow, Vcol, workranges[i]);

		// validate
		double ytotal = 0.0;
		for (point p : y.region)
			ytotal += (future (y.distribution[p]) { y[p] }).force();

		double refval[] = { 6.195840478801247E-4, 0.1249404059302216, 0.0 };
		double dev = Math.abs(ytotal - refval[size]);
		if (dev > 1.0e-10) {
			System.out.println("Validation failed");
			System.out.println("ytotal = " + ytotal + "  " + dev + "  " + size);
			throw new Error("Validation failed");
		} else {
			System.out.println("Validation succeeded");
		}
		return true;
	}

	private static void mul(double value[.] val,
			double value[.] x,
			double [.] yt,
			int value[.] row,
			int value[.] col,
			region sumrange) {
		for (point i : sumrange) {
			final int rowi = row[i];
			yt[rowi] = yt[rowi] + x[col[i]] * val[i];
		}
	}

	// trivial helper
	private static double value[.] RandomVector(dist d) {
		return new double value[d] (point[i]) {
			return R.nextDouble() * 1e-6;
		};
	}
} // end of SparseMatmultAllValuesClean.x10

