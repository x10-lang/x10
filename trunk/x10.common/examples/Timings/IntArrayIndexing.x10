/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Synthetic benchmark to time arary accesses.
 */
public class IntArrayIndexing extends x10Test {

	String _tests[] = { "testDouble" };

	static final boolean verbose = false;

	int [.] _intArray1D;
	int [.] _intArray2D;
	int [.] _intArray3D;
	int [.] _intArray4D;

	public IntArrayIndexing() {
		final int kArraySize = 30;
		region range1D,range2D,range3D,range4D;

		// Note: cannot do anything fancy with starting index--assume 0 based
		range1D = [0:kArraySize];
		range2D = [0:kArraySize,0:kArraySize];
		range4D = [0:2,0:4,2:10,1:10];
		//range3D = [0:13,0:37,0:19]; // make it zero based
		range3D = [0:11,0:6,0:7]; // reducing size further

		long start = System.currentTimeMillis();
		_intArray1D = new int[dist.factory.constant(range1D,here)];
		_intArray2D = new int[dist.factory.constant(range2D,here)];
		_intArray3D = new int[dist.factory.constant(range3D,here)];
		_intArray4D = new int[dist.factory.constant(range4D,here)];
		long stop = System.currentTimeMillis();
		System.out.println("int arrays allocated in "+((double)(stop-start)/1000)+ "seconds");
	}

	boolean verify3D(int [.] array) {

		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high();

		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();

		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j)
				for (int k = l3; k <= h3; ++k) {
					//System.out.println("value is:"+array[i,j,k]);
					array[i,j,k] = array[i,j,k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if ( array[i,j,k] != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i,j,k]+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(int [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high();
		int h4 = array.distribution.region.rank(3).high();
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int l4 = array.distribution.region.rank(3).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j)
				for (int k = l3; k <= h3; ++k)
					for (int l = l4; l <= h4; ++l) {
						array[i,j,k,l] = array[i,j,k,l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if ( array[i,j,k,l] != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i,j,k,l]+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	void initialize(int [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}

	boolean runIntTests(int repeatCount) {
		System.out.println("Testing Ints...");
		long start = System.currentTimeMillis();
		initialize(_intArray3D);
		while (repeatCount-- > 0)
			if (!verify3D(_intArray3D)) return false;

		System.out.println("Testing of 3D int arrays took "+((double)(System.currentTimeMillis()-start)/1000));
		initialize(_intArray4D);
		if (false) {
			while (repeatCount-- > 0)
				if (!verify4D(_intArray4D)) return false;
		}

		long stop = System.currentTimeMillis();
		System.out.println("Testing of int arrays took "+((double)(stop-start)/1000));
		return true;
	}

	public boolean run() {
		int repeatCount = 4000;

		if (!runIntTests(repeatCount)) return false;

		return true;
	}

	public static void main(String[] args) {
		new IntArrayIndexing().execute();
	}

	static class Generic {
		public int value;
	}
}

