/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Testing miscellaneous array declarations and initializations.
 *
 * @author kemal 4/2005
 */
public class ArrayDecl extends x10Test {

	public const N: int = 24;

	public def run(): boolean = {
		final val ia0: Array[int]{onePlace==here} = new Array[int](Dist.makeConstant([0..N-1], here));
		final val p: place = here;
		chk(ia0.dist.equals(Dist.makeConstant([0..N-1], p)));
		chk(ia0.dist.equals(Dist.makeConstant([0..N-1], p)));
		finish ateach (val (i): point in ia0) chk(ia0(i) == 0);

		final val v_ia2: Array[int] = new Array[int](Dist.makeConstant([0..N-1], here), (var point [i]: point): int => { return i; });
		chk(v_ia2.dist.equals(Dist.makeConstant([0..N-1], here)));
		for (val (i): point in v_ia2) chk(v_ia2(i) == i);

		final val ia2: Array[byte] = new Array[byte](Dist.makeConstant([0..N-1], (here).prev().prev()));
		chk(ia2.dist.equals(Dist.makeConstant([0..N-1], (here).prev().prev())));
		finish ateach (val (i): point in ia2) chk(ia2(i) == (byte)0);

		//Examples similar to section 10.3 of X10 reference manual

		final val data1: Array[double] = new Array[double](Dist.makeConstant([0..16], here), new doubleArray.pointwiseOp() {
				public def apply(var p: point[i]): double = { return (double)i; }
			});
		chk(data1.dist.equals(Dist.makeConstant([0..16], here)));
		for (val (i): point in data1) chk(data1(i) == (double)i);

		final val myStr: String = "abcdefghijklmnop";
		final val data2: Array[char] = new Array[char](Dist.makeConstant([1..2, 1..3], here), (var p: point[i,j]): char => { return myStr.charAt(i*j); });
		chk(data2.dist.equals(Dist.makeConstant([1..2, 1..3], here)));
		for (val (i,j): point in data2) chk(data2(i, j) == myStr.charAt(i*j));

		// is a region R converted to R->here in a dist context?
		//final long[.] data3 = new long[1:11]
		final val data3: Array[long] = new Array[long](Dist.makeConstant([1..11], here), (var point [i]: point): long => { return (long)i*i; });
		chk(data3.dist.equals(Dist.makeConstant([1..11], here)));
		for (val (i): point in data3) chk(data3(i) == (long)i*i);

		final val D: dist = dist.factory.random([0..9]);
		final val d: Array[float]{distribution==D} = new Array[float](D, (var point [i]: point): float => { return (float)(10.0*i); });
		chk(d.dist.equals(D));
		finish ateach (val (i): point in d) chk(d(i) == (float)(10.0*i));

		final val E: dist = dist.factory.random([1..7, 0..1]);
		final val result1: Array[short]{distribution==E} = new Array[short](E, (var point [i,j]: point): short => { return (short)(i+j); });
		chk(result1.dist.equals(E));
		finish ateach (val (i,j): point in E) chk(result1(i, j) == (short)(i+j));

		final val result2: Array[complex] = new Array[complex](Dist.makeConstant([0..N-1], here), (var point [i]: point): complex => { return new complex(i*N,-i); });
		chk(result2.dist.equals(Dist.makeConstant([0..N-1], here)));
		finish ateach (val (i): point in result2) chk(result2(i) == new complex(i*N,-i));

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayDecl().execute();
	}/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing miscellaneous array declarations and initializations.
 *
 * @author kemal 4/2005
 */
public class ArrayDecl extends x10Test {

	const int N = 24;

	public boolean run() {
		final int[:onePlace==here] ia0 = new int[[0:N-1]->here];
		final place p = here;
		chk(ia0.dist.equals([0:N-1]->p));
		chk(ia0.dist.equals([0:N-1]->p));
		finish ateach (point [i]: ia0) chk(ia0[i] == 0);

		final int value[.] v_ia2 = new int value[[0:N-1]->here]
			(point [i]) { return i; };
		chk(v_ia2.dist.equals([0:N-1]->here));
		for (point [i]: v_ia2) chk(v_ia2[i] == i);

		final byte[.] ia2 = new byte[[0:N-1]->(here).prev().prev()];
		chk(ia2.dist.equals([0:N-1]->(here).prev().prev()));
		finish ateach (point [i]: ia2) chk(ia2[i] == (byte)0);

		//Examples similar to section 10.3 of X10 reference manual

		final double[.] data1 = new double[[0:16]->here]
			new doubleArray.pointwiseOp() {
				public double apply(point p[i]) { return (double)i; }
			};
		chk(data1.dist.equals([0:16]->here));
		for (point [i]: data1) chk(data1[i] == (double)i);

		final String myStr="abcdefghijklmnop";
		final char value[.] data2 = new char value[[1:2,1:3]->here]
			(point p[i,j]) { return myStr.charAt(i*j); };
		chk(data2.dist.equals([1:2,1:3]->here));
		for (point [i,j]: data2) chk(data2[i,j] == myStr.charAt(i*j));

		// is a region R converted to R->here in a dist context?
		//final long[.] data3 = new long[1:11]
		final long[.] data3 = new long[[1:11]->here]
			(point [i]) { return (long)i*i; };
		chk(data3.dist.equals([1:11]->here));
		for (point [i]: data3) chk(data3[i] == (long)i*i);

		final dist D = dist.factory.random([0:9]);
		final float[:distribution==D] d = new float[D]
			(point [i]) { return (float)(10.0*i); };
		chk(d.dist.equals(D));
		finish ateach (point [i]: d) chk(d[i] == (float)(10.0*i));

		final dist E = dist.factory.random([1:7,0:1]);
		final short[:distribution==E] result1 = new short[E]
			(point [i,j]) { return (short)(i+j); };
		chk(result1.dist.equals(E));
		finish ateach (point [i,j]: E) chk(result1[i,j] == (short)(i+j));

		final complex[.] result2 = new complex[[0:N-1]->here]
			(point [i]) { return new complex(i*N,-i); };
		chk(result2.dist.equals([0:N-1]->here));
		finish ateach (point [i]: result2) chk(result2[i] == new complex(i*N,-i));

		return true;
	}

	public static void main(String[] args) {
		new ArrayDecl().execute();
	}

	final static value complex extends x10.lang.Object*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing miscellaneous array declarations and initializations.
 *
 * @author kemal 4/2005
 */
public class ArrayDecl extends x10Test {

	const int N = 24;

	public boolean run() {
		final int[:onePlace==here] ia0 = new int[[0:N-1]->here];
		final place p = here;
		chk(ia0.dist.equals([0:N-1]->p));
		chk(ia0.dist.equals([0:N-1]->p));
		finish ateach (point [i]: ia0) chk(ia0[i] == 0);

		final int value[.] v_ia2 = new int value[[0:N-1]->here]
			(point [i]) { return i; };
		chk(v_ia2.dist.equals([0:N-1]->here));
		for (point [i]: v_ia2) chk(v_ia2[i] == i);

		final byte[.] ia2 = new byte[[0:N-1]->(here).prev().prev()];
		chk(ia2.dist.equals([0:N-1]->(here).prev().prev()));
		finish ateach (point [i]: ia2) chk(ia2[i] == (byte)0);

		//Examples similar to section 10.3 of X10 reference manual

		final double[.] data1 = new double[[0:16]->here]
			new doubleArray.pointwiseOp() {
				public double apply(point p[i]) { return (double)i; }
			};
		chk(data1.dist.equals([0:16]->here));
		for (point [i]: data1) chk(data1[i] == (double)i);

		final String myStr="abcdefghijklmnop";
		final char value[.] data2 = new char value[[1:2,1:3]->here]
			(point p[i,j]) { return myStr.charAt(i*j); };
		chk(data2.dist.equals([1:2,1:3]->here));
		for (point [i,j]: data2) chk(data2[i,j] == myStr.charAt(i*j));

		// is a region R converted to R->here in a dist context?
		//final long[.] data3 = new long[1:11]
		final long[.] data3 = new long[[1:11]->here]
			(point [i]) { return (long)i*i; };
		chk(data3.dist.equals([1:11]->here));
		for (point [i]: data3) chk(data3[i] == (long)i*i);

		final dist D = dist.factory.random([0:9]);
		final float[:distribution==D] d = new float[D]
			(point [i]) { return (float)(10.0*i); };
		chk(d.dist.equals(D));
		finish ateach (point [i]: d) chk(d[i] == (float)(10.0*i));

		final dist E = dist.factory.random([1:7,0:1]);
		final short[:distribution==E] result1 = new short[E]
			(point [i,j]) { return (short)(i+j); };
		chk(result1.dist.equals(E));
		finish ateach (point [i,j]: E) chk(result1[i,j] == (short)(i+j));

		final complex[.] result2 = new complex[[0:N-1]->here]
			(point [i]) { return new complex(i*N,-i); };
		chk(result2.dist.equals([0:N-1]->here));
		finish ateach (point [i]: result2) chk(result2[i] == new complex(i*N,-i));

		return true;
	}

	public static void main(String[] args) {
		new ArrayDecl().execute();
	}

	final static value complex extends x10.lang.Object {
		var re: int;
		var im: int;
		public def this(var re: int, var im: int): complex = { this.re = re; this.im = im; }
	}
}
