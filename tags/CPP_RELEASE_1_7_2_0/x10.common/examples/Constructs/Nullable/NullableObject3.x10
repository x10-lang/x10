/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//Generated automatically by
//m4 NullableObject3.m4 > NullableObject3.x10
//Do not edit
import harness.x10Test;

/**
 * Class cast test for nullable types.
 *
 * In X10, nullable<Object>is a proper supertype of Object
 * (the latter does not include null, the former does).
 *
 * Tests miscellaneous classcast behavior with nullable types.
 *
 * @author kemal
 * 1/2005
 */
public class NullableObject3 extends x10Test {

	public boolean run() {
		x10.lang.Object x = new x10.lang.Object();
		nullable<x10.lang.Object> y = null;
		boolean castable = true;

		x = new boxedInt(1);
		// x can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt> __y = (nullable<boxedInt>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can not be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// x can not be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong> __y = (nullable<boxedLong>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		x = new boxedLong(1);
		// x can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can not be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// x can not be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt> __y = (nullable<boxedInt>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// x can be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong> __y = (nullable<boxedLong>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		x = new x10.lang.Object();
		// x can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// x can not be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// x can not be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt> __y = (nullable<boxedInt>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// x can not be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// x can not be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong> __y = (nullable<boxedLong>)x;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		y = null;
		// y can not be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can be cast to (nullable<x10>.lang.Object)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can not be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt> __y = (nullable<boxedInt>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can not be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		y = new boxedInt(1);
		// y can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt>__y = (nullable<boxedInt>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can not be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can not be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		y = new boxedLong(1);
		// y can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can not be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can not be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt>__y = (nullable<boxedInt>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		y = new x10.lang.Object();
		// y can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// y can not be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can not be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt>__y = (nullable<boxedInt>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can not be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// y can not be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)y;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		boxedInt i = new boxedInt(1);
		// i can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)i;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// i can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)i;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// i can be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)i;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// i can be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt>__y = (nullable<boxedInt>)i;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		nullable<boxedInt>j = null;
		// j can not be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// j can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// j can not be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// j can be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt>__y = (nullable<boxedInt>)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// (nullable<x10.lang.Object>)j can be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)(nullable<x10.lang.Object>)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		j = new boxedInt(1);
		// j can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// j can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// j can be cast to (boxedInt)
		castable = true;
		try {
			boxedInt __y = (boxedInt)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// j can be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt>__y = (nullable<boxedInt>)j;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		boxedLong l = new boxedLong(1);
		// l can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)l;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// l can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)l;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// l can be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)l;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// l can be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)l;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		nullable<boxedLong>m = null;
		// m can not be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// m can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// (nullable<x10.lang.Object>)m can be cast to (nullable<boxedInt>)
		castable = true;
		try {
			nullable<boxedInt>__y = (nullable<boxedInt>)(nullable<x10.lang.Object>)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// m can not be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (castable) throw new Error();

		// m can be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		m = new boxedLong(1);
		// m can be cast to (x10.lang.Object)
		castable = true;
		try {
			x10.lang.Object __y = (x10.lang.Object)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// m can be cast to (nullable<x10.lang.Object>)
		castable = true;
		try {
			nullable<x10.lang.Object> __y = (nullable<x10.lang.Object>)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// m can be cast to (boxedLong)
		castable = true;
		try {
			boxedLong __y = (boxedLong)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		// m can be cast to (nullable<boxedLong>)
		castable = true;
		try {
			nullable<boxedLong>__y = (nullable<boxedLong>)m;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (!castable) throw new Error();

		return true;
	}

	public static void main(String[] args) {
		new NullableObject3().execute();
	}

	/**
	 * Helper class -- boxed int
	 */
	static class boxedInt extends x10.lang.Object {
		int val;
		boxedInt(int x) { val = x; }
	}

	/**
	 * Helper class -- boxed long
	 */
	static class boxedLong extends x10.lang.Object {
		long val;
		boxedLong(long x) { val = x; }
	}

	static class X {
		static void use( nullable<x10.lang.Object> y) { }
	}
}

