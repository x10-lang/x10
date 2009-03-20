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
 * @author Christian Grothoff
 */
public class SparseMatmultAllValues extends x10Test {

	// DRIVER
	static final long RANDOM_SEED = 10101010;
	static final int size = 0;
	static final int[] datasizes_M = { 500, 100000, 500000 };
	static final int[] datasizes_N = { 500, 100000, 500000 };
	static final int[] datasizes_nz = { 2500, 500000, 2500000 };
	static final int SPARSE_NUM_ITER = 200;
	static final Random R = new Random(RANDOM_SEED);

	public static void main(String[] args) {
		new SparseMatmultAllValues().execute();
	}

	public SparseMatmultAllValues() { }

	int pos; // hack

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
				return 0; // Math.abs(R.nextInt()) % datasizes_M[size];
			};
		final int[.] col
			= new int[r_nz->here] (point[i]) {
				return 0; // Math.abs(R.nextInt()) % datasizes_N[size];
			};

		// val is the representation of the sparse MxN matrix;
		// val[i,j] corresponds to matrix[row[i],col[j]]
		final double [.] val
			= new double[r_nz->here] (point[i]) {
				return 0.0; // R.nextDouble();
			};

		// yucky original initialization code (also partially wrong -- % ds_M vs % ds_N for col!)

		// I would init and define x much later (but we must do it in order
		// given by JGF)
		// "x" value with which matrix is going to be multiplied
		final double value[.] x = RandomVector(d_N);

		for (int i = 0; i < datasizes_nz[size]; i++) {
			final int ds_M = datasizes_M[size];
			final int random_1 = R.nextInt();	// generate random row index (0, M-1)
			final int random_2 = R.nextInt(); // generate random column index (0, N-1)
			final double random_3 = R.nextDouble();
			final int i_final = i;

			row[i_final] = Math.abs(random_1) % ds_M;
			col[i_final] = Math.abs(random_2) % ds_M;
			val[i_final] = random_3;
		}

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

		// "y" is the vector with which is supposed to become y : = mat(val) * x
		final double[.] y = new double[d_M];

		finish ateach (final point p[i] : d_places)
			mul(Vval, x, y, Vrow, Vcol, SPARSE_NUM_ITER, workranges[i]);

		// validate
		double ytotal = 0.0;
		for (point p : y.region) {
			// CG-style: //    ytotal += (future (y.distribution[p]) { y[p] }).force();
		}
		for (point p[i] : rowt.region) {
			// JGF-style:
			final int i_final = i;
			final int row_i = (future (rowt.distribution[i]) { rowt[i_final] }).force();
			ytotal += (future (y.distribution[row_i]) { y[row_i] }).force();
		}
		double refval[] = { 0.7379886692958086, 150.0130719633895, 749.5245870753752 };
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
							int num_ITERATIONS,
							region sumrange)
	{
		for (int reps = 0; reps < num_ITERATIONS; reps++)
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
} // end of SparseMatmultAllValues.x10

