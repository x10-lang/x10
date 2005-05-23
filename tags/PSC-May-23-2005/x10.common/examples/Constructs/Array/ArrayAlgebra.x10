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
        int[.] makeArray(dist D, final int k)  {
         	return new int[D] (point p) { return k;};
        }
	public boolean run() {
		
		final dist D=dist.factory.blockCyclic([0:N-1],2);
		final dist D01=D|[0:N/2-1];
		final dist D23=D|[(N/2):N-1];
		final dist D0=D|[0:N/4-1];
		final dist D1=D|[(N/4):N/2-1];
		final dist D2=D|[(N/2):3*N/4-1];
		final dist D3=D|[(3*N/4):N-1];
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
	 * content and dist
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
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayAlgebra()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


}
