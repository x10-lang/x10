/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Synthetic benchmark to time array accesses.
 */
public class ArrayIndexing extends x10Test {
	String _tests[] = { "testDouble" };

	static final boolean verbose = false;

	double [.] _doubleArray1D;
	double [.] _doubleArray2D;
	double [.] _doubleArray3D;
	double [.] _doubleArray4D;

	int [.] _intArray1D;
	int [.] _intArray2D;
	int [.] _intArray3D;
	int [.] _intArray4D;

	long [.] _longArray3D;
	long [.] _longArray4D;

	float [.] _floatArray3D;
	float [.] _floatArray4D;

	char [.] _charArray3D;
	char [.] _charArray4D;

	byte [.] _byteArray3D;
	byte [.] _byteArray4D;

	Generic [.] _genericArray1D;
	Generic [.] _genericArray2D;
	Generic [.] _genericArray3D;
	Generic [.] _genericArray4D;

	public ArrayIndexing() {
		final int kArraySize = 30;
		region range1D, range2D, range3D, range4D;

		// Note: cannot do anything fancy with starting index--assume 0 based
		range1D = [0:kArraySize];
		range2D = [0:kArraySize, 0:kArraySize];
		range3D = [1:4, 3:4, 1:20];
		range4D = [0:2, 0:4, 2:10, 1:10];

		System.out.println("Testing double arrays...");
		long start = System.currentTimeMillis();
		_doubleArray1D = new double[dist.factory.constant(range1D, here)];
		_doubleArray2D = new double[dist.factory.constant(range2D, here)];
		_doubleArray3D = new double[dist.factory.constant(range3D, here)];
		_doubleArray4D = new double[dist.factory.constant(range4D, here)];
		long stop = System.currentTimeMillis();
		System.out.println("Double arrays allocated in "+((double)(stop-start)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_intArray1D = new int[dist.factory.constant(range1D, here)];
		_intArray2D = new int[dist.factory.constant(range2D, here)];
		_intArray3D = new int[dist.factory.constant(range3D, here)];
		_intArray4D = new int[dist.factory.constant(range4D, here)];
		stop = System.currentTimeMillis();
		System.out.println("int arrays allocated in "+((double)(stop-start)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_longArray3D = new long[dist.factory.constant(range3D, here)];
		_longArray4D = new long[dist.factory.constant(range4D, here)];
		stop = System.currentTimeMillis();
		System.out.println("long arrays allocated in "+((double)(stop-start)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_floatArray3D = new float[dist.factory.constant(range3D, here)];
		_floatArray4D = new float[dist.factory.constant(range4D, here)];
		stop = System.currentTimeMillis();
		System.out.println("float arrays allocated in "+((double)(stop-start)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_charArray3D = new char[dist.factory.constant(range3D, here)];
		_charArray4D = new char[dist.factory.constant(range4D, here)];
		stop = System.currentTimeMillis();
		System.out.println("char arrays allocated in "+((double)(stop-start)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_byteArray3D = new byte[dist.factory.constant(range3D, here)];
		_byteArray4D = new byte[dist.factory.constant(range4D, here)];
		stop = System.currentTimeMillis();
		System.out.println("byte arrays allocated in "+((double)(stop-start)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_genericArray1D = new Generic[dist.factory.constant(range1D, here)];
		_genericArray2D = new Generic[dist.factory.constant(range2D, here)];
		_genericArray3D = new Generic[dist.factory.constant(range3D, here)];
		_genericArray4D = new Generic[dist.factory.constant(range4D, here)];
		stop = System.currentTimeMillis();
		System.out.println("Generic arrays allocated in "+((double)(stop-start)/1000)+ "seconds");
	}

	boolean verify1D(Generic [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int l1 = array.distribution.region.rank(0).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i) {
			array[i] = array[i];
			if (verbose) System.out.println("a["+i+"] = "+count);
			if (array[i].value != count) {
				System.out.println("failed a["+i+"] ("+array[i].value+") != "+count);
				return false;
			}
			++count;
		}
		return true;
	}
	boolean verify2D(Generic [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j) {
				array[i, j] = array[i, j];
				if (verbose) System.out.println("a["+i+","+j+"] = "+count);
				if (array[i, j].value != count) {
					System.out.println("failed a["+i+","+j+"] ("+array[i, j].value+") != "+count);
					return false;
				}
				++count;
			}
		return true;
	}
	boolean verify3D(Generic [.] array) {
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
					array[i, j, k] = array[i, j, k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array[i, j, k].value != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i, j, k].value+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(Generic [.] array) {
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
						array[i, j, k, l] = array[i, j, k, l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array[i, j, k, l].value != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i, j, k, l].value+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	boolean verify3D(double [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high() ;
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j)
				for (int k = l3; k <= h3; ++k) {
					array[i, j, k] = array[i, j, k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array[i, j, k] != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i, j, k]+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(double [.] array) {
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
						array[i, j, k, l] = array[i, j, k, l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array[i, j, k, l] != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i, j, k, l]+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	boolean verify3D(long [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high() ;
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j)
				for (int k = l3; k <= h3; ++k) {
					array[i, j, k] = array[i, j, k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array[i, j, k] != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i, j, k]+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(long [.] array) {
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
						array[i, j, k, l] = array[i, j, k, l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array[i, j, k, l] != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i, j, k, l]+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	boolean verify3D(float [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high() ;
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j)
				for (int k = l3; k <= h3; ++k) {
					array[i, j, k] = array[i, j, k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array[i, j, k] != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i, j, k]+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(float [.] array) {
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
						array[i, j, k, l] = array[i, j, k, l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array[i, j, k, l] != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i, j, k, l]+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	boolean verify3D(char [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high() ;
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j)
				for (int k = l3; k <= h3; ++k) {
					array[i, j, k] = array[i, j, k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+(char)count);
					if (array[i, j, k] != (char)count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i, j, k]+") != "+(char)count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(char [.] array) {
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
						array[i, j, k, l] = array[i, j, k, l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+(char)count);
						if (array[i, j, k, l] != (char)count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i, j, k, l]+") != "+(char)count);
							return false;
						}
						++count;
					}
		return true;
	}

	boolean verify3D(byte [.] array) {
		int h1 = array.distribution.region.rank(0).high();
		int h2 = array.distribution.region.rank(1).high();
		int h3 = array.distribution.region.rank(2).high() ;
		int l1 = array.distribution.region.rank(0).low();
		int l2 = array.distribution.region.rank(1).low();
		int l3 = array.distribution.region.rank(2).low();
		int count = 0;
		for (int i = l1; i <= h1; ++i)
			for (int j = l2; j <= h2; ++j)
				for (int k = l3; k <= h3; ++k) {
					array[i, j, k] = array[i, j, k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+(byte)count);
					if (array[i, j, k] != (byte)count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i, j, k]+") != "+(byte)count);
						return false;
					}
					++count;
				}
		return true;
	}
	boolean verify4D(byte [.] array) {
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
						array[i, j, k, l] = array[i, j, k, l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+(byte)count);
						if (array[i, j, k, l] != (byte)count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i, j, k, l]+") != "+(byte)count);
							return false;
						}
						++count;
					}
		return true;
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
					//System.out.println("value is:"+array[i, j, k]);
					array[i, j, k] = array[i, j, k];
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array[i, j, k] != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array[i, j, k]+") != "+count);
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
						array[i, j, k, l] = array[i, j, k, l]; // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array[i, j, k, l] != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array[i, j, k, l]+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	void initialize(double [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	void initialize(Generic [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = new Generic();
			array[p].value = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	void initialize(int [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	void initialize(long [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	void initialize(float [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	void initialize(byte [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = (byte)(count++);
			if (verbose) System.out.println("init:"+p+" = "+(byte)count);
		}
	}
	void initialize(char [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = (char)(count++);
			if (verbose) System.out.println("init:"+p+" = "+(char)count);
		}
	}
	void initialize(boolean [.] array) {
		dist arrayDist = array.distribution;
		int count = 0;
		for (point p:array.distribution.region) {
			array[p] = 1 == (count++)%2;
			if (verbose) System.out.println("init:"+p+" = "+(1 == count%2));
		}
	}

	boolean runDoubleTests(int repeatCount) {
		System.out.println("Testing Doubles...");
		long start = System.currentTimeMillis();
		initialize(_doubleArray3D);
		if (!verify3D(_doubleArray3D)) return false;

		initialize(_doubleArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_doubleArray4D)) return false;

		long stop = System.currentTimeMillis();
		System.out.println("Testing of double arrays took "+((double)(stop-start)/1000));
		return true;
	}

	boolean runFloatTests(int repeatCount) {
		System.out.println("Testing Floats...");
		long start = System.currentTimeMillis();
		initialize(_floatArray3D);
		if (!verify3D(_floatArray3D)) return false;

		initialize(_floatArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_floatArray4D)) return false;

		long stop = System.currentTimeMillis();
		System.out.println("Testing of float arrays took "+((double)(stop-start)/1000));
		return true;
	}

	boolean runIntTests(int repeatCount) {
		System.out.println("Testing Ints...");
		long start = System.currentTimeMillis();
		initialize(_intArray3D);
		if (!verify3D(_intArray3D)) return false;

		initialize(_intArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_intArray4D)) return false;

		long stop = System.currentTimeMillis();
		System.out.println("Testing of int arrays took "+((double)(stop-start)/1000));
		return true;
	}

	boolean runLongTests(int repeatCount) {
		System.out.println("Testing Longs...");
		long start = System.currentTimeMillis();
		initialize(_longArray3D);
		if (!verify3D(_longArray3D)) return false;

		initialize(_longArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_longArray4D)) return false;

		long stop = System.currentTimeMillis();
		System.out.println("Testing of long arrays took "+((double)(stop-start)/1000));
		return true;
	}

	boolean runByteTests(int repeatCount) {
		System.out.println("Testing Bytes...");
		long start = System.currentTimeMillis();
		initialize(_byteArray3D);
		if (!verify3D(_byteArray3D)) return false;

		initialize(_byteArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_byteArray4D)) return false;

		long stop = System.currentTimeMillis();
		System.out.println("Testing of byte arrays took "+((double)(stop-start)/1000));
		return true;
	}

	boolean runCharTests(int repeatCount) {
		System.out.println("Testing Chars...");
		long start = System.currentTimeMillis();
		initialize(_charArray3D);
		if (!verify3D(_charArray3D)) return false;

		initialize(_charArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_charArray4D)) return false;

		long stop = System.currentTimeMillis();
		System.out.println("Testing of char arrays took "+((double)(stop-start)/1000));
		return true;
	}

	boolean runBooleanTests(int repeatCount) {
		return true;
	}

	boolean runGenericTests(int repeatCount) {
		System.out.println("Testing Longs...");
		initialize(_genericArray1D);
		if (!verify1D(_genericArray1D)) return false;

		initialize(_genericArray2D);
		if (!verify2D(_genericArray2D)) return false;

		// just time 3 and 4D
		long start = System.currentTimeMillis();
		initialize(_genericArray3D);
		if (!verify3D(_genericArray3D)) return false;

		initialize(_genericArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_genericArray4D)) return false;

		long stop = System.currentTimeMillis();
		System.out.println("Testing of generic arrays took "+((double)(stop-start)/1000));
		return true;
	}

	public boolean run() {
		int repeatCount = 500;
		if (!runByteTests(repeatCount)) return false;
		if (!runCharTests(repeatCount)) return false;
		if (!runDoubleTests(repeatCount)) return false;
		if (!runFloatTests(repeatCount)) return false;
		if (!runIntTests(repeatCount)) return false;
		if (!runLongTests(repeatCount)) return false;
		if (!runBooleanTests(repeatCount)) return false;

		if (!runGenericTests(repeatCount)) return false;
		return true;
	}

	void testDouble2D(int iterations, boolean verbose) {
		//if (verbose) System.out.println("testDouble2d called with "+iterations);
		long start, stop;
		start = System.currentTimeMillis();
		for (int i = 0; i < iterations; ++i)
			for (int j = 0; j< iterations; ++j) {
				_doubleArray2D[i, j] = _doubleArray2D[i, j] + i+j;
			}
		stop = System.currentTimeMillis();
		if (verbose)System.out.println("testDouble2d("+iterations+") elapsed time:"+((double)(stop-start)/1000));
	}

	void testDouble1D(int iterations, boolean verbose) {
		if (verbose) System.out.println("testDouble1d called with "+iterations);
		long start, stop;
		start = System.currentTimeMillis();
		for (int i = 0; i < iterations; ++i) {
			_doubleArray1D[i] = _doubleArray1D[i] + i;
		}
		stop = System.currentTimeMillis();
		if (verbose)System.out.println("testDouble1d("+iterations+") elapsed time:"+((double)(stop-start)/1000));
	}

	public static void main(String[] args) {
		new ArrayIndexing().execute();
	}

	static class Generic {
		public int value;
	}
}

