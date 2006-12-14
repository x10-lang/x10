/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The code below runs as Java but does not compile as x10.  Referring to
 * the indicated line that initializes 'a' it gives,
 * MultiDimensionalJavaArray.x10:15: Cannot assign double[] to double[][].
 * It should compile cleanly.
 *
 * Submitted by Doug Lovell.
 * @author igor 1/2006
 */
public class MultiDimensionalJavaArray extends x10Test {

	private static final int MIN = 100;
	private static final int MAJ = 10;

	public boolean run() {
		double[][] a;
		a = new double[MAJ][MIN];
//		  ^ MultiDimensionalJavaArray.x10:15: Cannot assign double[] to double[][].
		for (int i = 0; i < MAJ; ++i)
			for (int j = 0; j < MIN; ++j) {
				a[i][j] = i * j / Math.PI;
			}
		double [] d = a[MAJ/2];
		for (int j = 0; j < MIN; ++j) {
			chk(d[j] == MAJ/2 * j / Math.PI);
		}
		return true;
	}

	public static void main(String[] args) {
		new MultiDimensionalJavaArray().execute();
	}
}

