/*
 * @author kemal 4/2005
 *
 * Constant promotions to arrays: (D n)
 * 
 * disjoint union and overlay of arrays
 *
 * array lift, scan and reduce. 
 *
 *
 */
public class ArrayAlgebra {

	const int N=24;
     int[.] makeArray(distribution D, final int k)  {
     	return new int[D] (point p) { return k;};
     }
	public boolean run() {
		
		final distribution D=distribution.factory.blockCyclic([0:N-1],2);
		final distribution D01=D|[0:N/2-1];
		final distribution D23=D|[(N/2):N-1];
		final distribution D0=D|[0:N/4-1];
		final distribution D1=D|[(N/4):N/2-1];
		final distribution D2=D|[(N/2):3*N/4-1];
		final distribution D3=D|[(3*N/4):N-1];
		final int[.] ia1=
		 makeArray(D, -99).overlay((makeArray(D01, -1) || makeArray(D23, -2))
		 		.overlay(makeArray(D3, 3)).overlay(makeArray(D0, 9)));
		arrEq(ia1|D0, makeArray(D0, 9));
		arrEq(ia1|D1, makeArray(D1, -1));
		arrEq(ia1|D2, makeArray(D2, -2));
		arrEq(ia1|D3, makeArray(D3, 3));
		chk(ia1.sum()==9*N/4);
		arrEq(ia1.scan(intArray.add,0),
		      new int[D](point [i])
		      {return (ia1|(0:i)).reduce(intArray.add,0);});
		arrEq(makeArray(D01, 1).lift(intArray.add,makeArray(D01, -4)),
		      makeArray(D01, -3)); 
		// are we still supporting +,-,... on arrays?
		arrEq(makeArray(D01, 1)+ makeArray(D01, -4),
		      makeArray(D01, -3)* makeArray(D01, 1)); 
		return true;
	}

	/**
	 * Throw an error iff x and y are not arrays with same
	 * content and distribution
	 */

	static void arrEq(final int[.] x,final int[.] y) {
		chk(x.distribution.equals(y.distribution));
		finish ateach(point p:x) chk(x[p]==y[p]);
	}	

 	/**
	 * Throw an error if b is false
	 */
	static void chk(boolean b) {

		if(!b) throw new Error();
	}

	/**
	 * Main method
	 */
	public static void main(String args[]) {
		boolean b= (new ArrayAlgebra()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
