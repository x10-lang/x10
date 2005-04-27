
/**
 * @author Kemal Ebcioglu 4/2005
 *
 * Testing complex async bodies  
 */
public class AsyncTest2  {


	public boolean run() {
		final int NP=place.MAX_PLACES;
		final int[.] A = new int[distribution.factory.unique()];
		finish
		for(point [k]:0:NP-1)
			async (A.distribution[k])
				ateach(point [i]: A) atomic A[i]+=i; 
		finish ateach(point [i]:A) {chk(A[i]==i*NP);}
		
		return true;
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
	}
	public static void main(String args[]) {
		boolean b= (new AsyncTest2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
