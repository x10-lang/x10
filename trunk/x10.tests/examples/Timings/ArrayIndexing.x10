/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Synthetic benchmark to time array accesses.
 */
public class ArrayIndexing extends x10Test {
	var _tests: Rail[String] = [ "testDouble" ];

	const verbose: boolean = false;

	var _doubleArray1D: Array[double];
	var _doubleArray2D: Array[double];
	var _doubleArray3D: Array[double];
	var _doubleArray4D: Array[double];

	var _intArray1D: Array[int];
	var _intArray2D: Array[int];
	var _intArray3D: Array[int];
	var _intArray4D: Array[int];

	var _longArray3D: Array[long];
	var _longArray4D: Array[long];

	var _floatArray3D: Array[float];
	var _floatArray4D: Array[float];

	var _charArray3D: Array[char];
	var _charArray4D: Array[char];

	var _byteArray3D: Array[byte];
	var _byteArray4D: Array[byte];

	var _genericArray1D: Array[Generic];
	var _genericArray2D: Array[Generic];
	var _genericArray3D: Array[Generic];
	var _genericArray4D: Array[Generic];

	public def this(): ArrayIndexing = {
		val kArraySize: int = 30;
		var range1D: region;
                var range2D: region;
                var range3D: region;
                var range4D: region;

		// Note: cannot do anything fancy with starting index--assume 0 based
		range1D = [0..kArraySize];
		range2D = [0..kArraySize, 0..kArraySize];
		range3D = [1..4, 3..4, 1..20];
		range4D = [0..2, 0..4, 2..10, 1..10];

		System.out.println("Testing double arrays...");
		var start: long = System.currentTimeMillis();
		_doubleArray1D = new Array[double](range1D->here);
		_doubleArray2D = new Array[double](range2D->here);
		_doubleArray3D = new Array[double](range3D->here);
		_doubleArray4D = new Array[double](range4D->here);
		var stop: long = System.currentTimeMillis();
		System.out.println("Double arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_intArray1D = new Array[int](range1D->here);
		_intArray2D = new Array[int](range2D->here);
		_intArray3D = new Array[int](range3D->here);
		_intArray4D = new Array[int](range4D->here);
		stop = System.currentTimeMillis();
		System.out.println("int arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_longArray3D = new Array[long](range3D->here);
		_longArray4D = new Array[long](range4D->here);
		stop = System.currentTimeMillis();
		System.out.println("long arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_floatArray3D = new Array[float](range3D->here);
		_floatArray4D = new Array[float](range4D->here);
		stop = System.currentTimeMillis();
		System.out.println("float arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_charArray3D = new Array[char](range3D->here);
		_charArray4D = new Array[char](range4D->here);
		stop = System.currentTimeMillis();
		System.out.println("char arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_byteArray3D = new Array[byte](range3D->here);
		_byteArray4D = new Array[byte](range4D->here);
		stop = System.currentTimeMillis();
		System.out.println("byte arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");

		start = System.currentTimeMillis();
		_genericArray1D = new Array[Generic](range1D->here);
		_genericArray2D = new Array[Generic](range2D->here);
		_genericArray3D = new Array[Generic](range3D->here);
		_genericArray4D = new Array[Generic](range4D->here);
		stop = System.currentTimeMillis();
		System.out.println("Generic arrays allocated in "+(((stop-start) to double)/1000)+ "seconds");
	}

	def verify1D(var array: Array[Generic]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var l1: int = array.dist.region.rank(0).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i) {
			array(i) = array(i);
			if (verbose) System.out.println("a["+i+"] = "+count);
			if (array(i).value != count) {
				System.out.println("failed a["+i+"] ("+array(i).value+") != "+count);
				return false;
			}
			++count;
		}
		return true;
	}
	def verify2D(var array: Array[Generic]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j) {
				array(i, j) = array(i, j);
				if (verbose) System.out.println("a["+i+","+j+"] = "+count);
				if (array(i, j).value != count) {
					System.out.println("failed a["+i+","+j+"] ("+array(i, j).value+") != "+count);
					return false;
				}
				++count;
			}
		return true;
	}
	def verify3D(var array: Array[Generic]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array(i, j, k).value != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k).value+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[Generic]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array(i, j, k, l).value != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l).value+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	def verify3D(var array: Array[double]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high() ;
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array(i, j, k) != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[double]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array(i, j, k, l) != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	def verify3D(var array: Array[long]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high() ;
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array(i, j, k) != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[long]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array(i, j, k, l) != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	def verify3D(var array: Array[float]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high() ;
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array(i, j, k) != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[float]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array(i, j, k, l) != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	def verify3D(var array: Array[char]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high() ;
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+ (count to char));
					if (array(i, j, k) != (count to char)) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+(count to char));
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[char]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+(count to char));
						if (array(i, j, k, l) != (count to char)) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+(count to char));
							return false;
						}
						++count;
					}
		return true;
	}

	def verify3D(var array: Array[byte]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high() ;
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+(count to byte));
					if (array(i, j, k) != (count to byte)) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+(count to byte));
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[byte]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+(count to byte));
						if (array(i, j, k, l) != (count to byte)) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+(count to byte));
							return false;
						}
						++count;
					}
		return true;
	}

	def verify3D(var array: Array[int]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k) {
					//System.out.println("value is:"+array[i, j, k]);
					array(i, j, k) = array(i, j, k);
					if (verbose) System.out.println("a["+i+","+j+","+k+"] = "+count);
					if (array(i, j, k) != count) {
						System.out.println("failed a["+i+","+j+","+k+"] ("+array(i, j, k)+") != "+count);
						return false;
					}
					++count;
				}
		return true;
	}
	def verify4D(var array: Array[int]): boolean = {
		var h1: int = array.dist.region.rank(0).high();
		var h2: int = array.dist.region.rank(1).high();
		var h3: int = array.dist.region.rank(2).high();
		var h4: int = array.dist.region.rank(3).high();
		var l1: int = array.dist.region.rank(0).low();
		var l2: int = array.dist.region.rank(1).low();
		var l3: int = array.dist.region.rank(2).low();
		var l4: int = array.dist.region.rank(3).low();
		var count: int = 0;
		for (var i: int = l1; i <= h1; ++i)
			for (var j: int = l2; j <= h2; ++j)
				for (var k: int = l3; k <= h3; ++k)
					for (var l: int = l4; l <= h4; ++l) {
						array(i, j, k, l) = array(i, j, k, l); // ensure set works as well
						if (verbose) System.out.println("a["+i+","+j+","+k+","+l+"] = "+count);
						if (array(i, j, k, l) != count) {
							System.out.println("failed a["+i+","+j+","+k+","+l+"] ("+array(i, j, k, l)+") != "+count);
							return false;
						}
						++count;
					}
		return true;
	}

	def initialize(var array: Array[double]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	def initialize(var array: Array[Generic]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = new Generic();
			array(p).value = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	def initialize(var array: Array[int]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	def initialize(var array: Array[long]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	def initialize(var array: Array[float]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = count++;
			if (verbose) System.out.println("init:"+p+" = "+count);
		}
	}
	def initialize(var array: Array[byte]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = (count++) to byte;
			if (verbose) System.out.println("init:"+p+" = "+(count to byte));
		}
	}
	def initialize(var array: Array[char]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = (count++) to char;
			if (verbose) System.out.println("init:"+p+" = "+(count to char));
		}
	}
	def initialize(var array: Array[boolean]): void = {
		var arrayDist: dist = array.dist;
		var count: int = 0;
		for (val p: point in array.dist.region) {
			array(p) = 1 == (count++)%2;
			if (verbose) System.out.println("init:"+p+" = "+(1 == count%2));
		}
	}

	def runDoubleTests(var repeatCount: int): boolean = {
		System.out.println("Testing Doubles...");
		var start: long = System.currentTimeMillis();
		initialize(_doubleArray3D);
		if (!verify3D(_doubleArray3D)) return false;

		initialize(_doubleArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_doubleArray4D)) return false;

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of double arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	def runFloatTests(var repeatCount: int): boolean = {
		System.out.println("Testing Floats...");
		var start: long = System.currentTimeMillis();
		initialize(_floatArray3D);
		if (!verify3D(_floatArray3D)) return false;

		initialize(_floatArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_floatArray4D)) return false;

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of float arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	def runIntTests(var repeatCount: int): boolean = {
		System.out.println("Testing Ints...");
		var start: long = System.currentTimeMillis();
		initialize(_intArray3D);
		if (!verify3D(_intArray3D)) return false;

		initialize(_intArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_intArray4D)) return false;

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of int arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	def runLongTests(var repeatCount: int): boolean = {
		System.out.println("Testing Longs...");
		var start: long = System.currentTimeMillis();
		initialize(_longArray3D);
		if (!verify3D(_longArray3D)) return false;

		initialize(_longArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_longArray4D)) return false;

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of long arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	def runByteTests(var repeatCount: int): boolean = {
		System.out.println("Testing Bytes...");
		var start: long = System.currentTimeMillis();
		initialize(_byteArray3D);
		if (!verify3D(_byteArray3D)) return false;

		initialize(_byteArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_byteArray4D)) return false;

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of byte arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	def runCharTests(var repeatCount: int): boolean = {
		System.out.println("Testing Chars...");
		var start: long = System.currentTimeMillis();
		initialize(_charArray3D);
		if (!verify3D(_charArray3D)) return false;

		initialize(_charArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_charArray4D)) return false;

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of char arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	def runBooleanTests(var repeatCount: int): boolean = {
		return true;
	}

	def runGenericTests(var repeatCount: int): boolean = {
		System.out.println("Testing Longs...");
		initialize(_genericArray1D);
		if (!verify1D(_genericArray1D)) return false;

		initialize(_genericArray2D);
		if (!verify2D(_genericArray2D)) return false;

		// just time 3 and 4D
		var start: long = System.currentTimeMillis();
		initialize(_genericArray3D);
		if (!verify3D(_genericArray3D)) return false;

		initialize(_genericArray4D);
		while (repeatCount-- > 0)
			if (!verify4D(_genericArray4D)) return false;

		var stop: long = System.currentTimeMillis();
		System.out.println("Testing of generic arrays took "+(((stop-start) to double)/1000));
		return true;
	}

	public def run(): boolean = {
		var repeatCount: int = 500;
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

	def testDouble2D(iterations: int, verbose: boolean): void = {
		//if (verbose) System.out.println("testDouble2d called with "+iterations);
		var start: long;
                var stop: long;
		start = System.currentTimeMillis();
		for (var i: int = 0; i < iterations; ++i)
			for (var j: int = 0; j< iterations; ++j) {
				_doubleArray2D(i, j) = _doubleArray2D(i, j) + i+j;
			}
		stop = System.currentTimeMillis();
		if (verbose)System.out.println("testDouble2d("+iterations+") elapsed time:"+(((stop-start) to double)/1000));
	}

	def testDouble1D(iterations: int, verbose: boolean): void = {
		if (verbose) System.out.println("testDouble1d called with "+iterations);
		var start: long;
                var stop: long;
		start = System.currentTimeMillis();
		for (var i: int = 0; i < iterations; ++i) {
			_doubleArray1D(i) = _doubleArray1D(i) + i;
		}
		stop = System.currentTimeMillis();
		if (verbose)System.out.println("testDouble1d("+iterations+") elapsed time:"+(((stop-start) to double)/1000));
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayIndexing().execute();
	}

	static class Generic {
		public var value: int;
	}
}
