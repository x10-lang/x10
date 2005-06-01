
/**
 * @author Kemal Ebcioglu 4/2005
 *
 * Testing complex async bodies  
 */
public class AsyncTest2  {


	public boolean run() {
		final int NP=place.MAX_PLACES;
		final int[.] A = new int[dist.factory.unique()];
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
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AsyncTest2()).run();
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
