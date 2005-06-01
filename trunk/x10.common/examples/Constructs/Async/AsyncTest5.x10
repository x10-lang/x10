
/**
 * @author Kemal Ebcioglu 4/2005
 *
 * When the async place is not specified, it is take to be 'here'
 */
public class AsyncTest5  {

	public boolean run() {
		final int[.] A = new int[dist.factory.unique()];
		chk(place.MAX_PLACES>=2);
		finish async chk(A.distribution[0]==here);
		// verify unique distribution
		for(point [i]:A)
		  for(point [j]:A)
		    chk(implies(A.distribution[i]==A.distribution[j],
		                i==j));

		// verify async S is async(here)S
		finish ateach(point [i]: A) {
		    async {atomic A[i]+=i;
		           chk(A.distribution[i]==here);
		           async(this) async chk(A.distribution[0]==here);
		           }
		}
		finish ateach(point [i]:A) {
			chk(A[i]==i);
		}
		return true;
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
	}
	static boolean implies(boolean x, boolean y) {
		return (!x)|y;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AsyncTest5()).run();
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
