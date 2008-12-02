/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing arrays with user-defined element types.
 *
 * @author Christian Grothoff
 * @author Kemal Ebcioglu
 */
public class UserDefinedArray extends x10Test {

	const region R = [0:1];
	const dist D = dist.factory.block(R);

	public boolean run() {
		chk(place.MAX_PLACES <= 1 || D[0] != D[1]);
		// create an array a such that
		// a[0] is in D[0] but points to an object in D[1]
		// and a[1] is in D[1] but points to an object in D[0]
		final E v1 = future(D[1]){new E(1)}.force();
		final E v2 = future(D[0]){new E(2)}.force();
		final E[.] a = new E[D] (point [i]) { return (i==0) ? v1 : v2; };

		chk(a.distribution[0] == D[0] &&
				future(a.distribution[0]){a[0]}.force() == v1 &&
				v1.getLocation() == D[1] &&
				future(v1){v1.v}.force() == 1);
		chk(a.distribution[1] == D[1] &&
				future(a.distribution[1]){a[1]}.force() == v2 &&
				v2.getLocation() == D[0] &&
				future(v2){v2.v}.force() == 2);
		//this top level future runs in D[1] since a[0]==v1 && v1.getLocation()==D[1]
		int i0 = future(future(a.distribution[0]){a[0]}.force().getLocation())
		{ future(a.distribution[0]){a[0]}.force().v }.force();
		//this top level future runs in D[0] since a[1]==v2 && v2.getLocation()==D[0]
		int i1 = future(future(a.distribution[1]){a[1]}.force())
		{ future(a.distribution[1]){a[1]}.force().v }.force();

		return i0 + 1 == i1;
	}

	public static void main(String[] args) {
		new UserDefinedArray().execute();
	}

	static class E extends x10.lang.Object {
		int v;
		E(int i) { v = i; }
	}
}

