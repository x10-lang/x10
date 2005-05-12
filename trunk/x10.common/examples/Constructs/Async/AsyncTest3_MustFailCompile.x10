

/**
 * @author Kemal 4/2005
 * Async with no place-- implicit syntax
 * For now, assume it should cause a compile time error
 * 
 * 
 */
public class AsyncTest3_MustFailCompile  {


	public boolean run() {
		final int[.] A=new int[dist.factory.unique()];
		chk(place.FIRST_PLACE.next()!=place.FIRST_PLACE);
		finish async {}
		//==> Compiler error expected unless
		//place inference is correctly done
		finish async {A[1]+=1; chk(here==A.distribution[1]);}
		//==> Compiler error definitely expected
		finish async {A[0]+=A[1];}
	  	return true;
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new AsyncTest3_MustFailCompile()).run();
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
