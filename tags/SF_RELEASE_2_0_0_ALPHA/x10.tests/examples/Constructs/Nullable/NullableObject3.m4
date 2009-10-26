define(`isCastable',
		`// $1 can ifelse($3,`no',not )be cast to ($2)
		castable = true;
		try {
			$2 __y = ($2)$1;
			X.use(__y);
		} catch (ClassCastException e) {
			castable = false;
		}
		if (ifelse($3,`yes',`!',`')castable) throw new Error();
')
//Generated automatically by
//m4 NullableObject3.m4 > NullableObject3.x10
//Do not edit
import harness.x10Test;

/**
 * Class cast test for nullable types.
 *
 * In X10, nullable Object is a proper supertype of Object
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
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable x10.lang.Object,yes)
		isCastable(x,boxedInt,yes)
		isCastable(x,nullable<boxedInt>,yes)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable boxedLong,no)

		x = new boxedLong(1);
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable<x10.lang.Object>,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable<boxedInt>,no)
		isCastable(x,boxedLong,yes)
		isCastable(x,nullable<boxedLong>,yes)

		x = new x10.lang.Object();
		isCastable(x,x10.lang.Object,yes)
		isCastable(x,nullable<x10.lang.Object>,yes)
		isCastable(x,boxedInt,no)
		isCastable(x,nullable<boxedInt>,no)
		isCastable(x,boxedLong,no)
		isCastable(x,nullable<boxedLong>,no)

		y = null;
		isCastable(y,x10.lang.Object,no)
		isCastable(y,nullable<x10.lang.Object,yes)
		isCastable(y,boxedInt,no)
		isCastable(y,nullable<boxedInt,yes)
		isCastable(y,boxedLong,no)
		isCastable(y,nullable<boxedLong,yes)

		y = new boxedInt(1);
		isCastable(y,x10.lang.Object,yes)
		isCastable(y,nullable<x10.lang.Object,yes)
		isCastable(y,boxedInt,yes)
		isCastable(y,nullable<boxedInt,yes)
		isCastable(y,boxedLong,no)
		isCastable(y,nullable<boxedLong,no)

		y = new boxedLong(1);
		isCastable(y,x10.lang.Object,yes)
		isCastable(y,nullable<x10.lang.Object>,yes)
		isCastable(y,boxedInt,no)
		isCastable(y,nullable<boxedInt>,no)
		isCastable(y,boxedLong,yes)
		isCastable(y,nullable<boxedLong>,yes)

		y = new x10.lang.Object();
		isCastable(y,x10.lang.Object>,yes)
		isCastable(y,nullable<x10.lang.Object>,yes)
		isCastable(y,boxedInt,no)
		isCastable(y,nullable<boxedInt>,no)
		isCastable(y,boxedLong,no)
		isCastable(y,nullable<boxedLong>,no)

		boxedInt i = new boxedInt(1);
		isCastable(i,x10.lang.Object,yes)
		isCastable(i,nullable<x10.lang.Object>,yes)
		isCastable(i,boxedInt,yes)
		isCastable(i,nullable<boxedInt>,yes)

		nullable boxedInt j = null;
		isCastable(j,x10.lang.Object,no)
		isCastable(j,nullable<x10.lang.Object>,yes)
		isCastable(j,boxedInt,no)
		isCastable(j,nullable<boxedInt>,yes)
		isCastable((nullable<x10.lang.Object>)j,nullable boxedLong,yes)

		j = new boxedInt(1);
		isCastable(j,x10.lang.Object,yes)
		isCastable(j,nullable<x10.lang.Object>,yes)
		isCastable(j,boxedInt,yes)
		isCastable(j,nullable<boxedInt>,yes)

		boxedLong l = new boxedLong(1);
		isCastable(l,x10.lang.Object,yes)
		isCastable(l,nullable<x10.lang.Object>,yes)
		isCastable(l,boxedLong,yes)
		isCastable(l,nullable<boxedLong>,yes)

		nullable boxedLong m = null;
		isCastable(m,x10.lang.Object,no)
		isCastable(m,nullable<x10.lang.Object>,yes)
		isCastable((nullable<x10.lang.Object>)m,nullable boxedInt,yes)
		isCastable(m,boxedLong,no)
		isCastable(m,nullable<boxedLong>,yes)

		m = new boxedLong(1);
		isCastable(m,x10.lang.Object,yes)
		isCastable(m,nullable<x10.lang.Object>,yes)
		isCastable(m,boxedLong,yes)
		isCastable(m,nullable<boxedLong>,yes)

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
		static void use( nullable java.lang.Object y) { }
	}
}

