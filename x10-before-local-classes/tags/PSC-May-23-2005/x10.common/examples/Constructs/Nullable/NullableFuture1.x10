import x10.lang.*;
/**
 * when nullable future T x is not null at run time
 * ((future T)x) should not cause exception
 * and ((future T).x).force() should return a T
 */
public class NullableFuture1 {
	public boolean run() {
		nullable future<int> x;
		if (X.t()) {
			x=future{42};
		} else {
			x=null;
		}
		return x!=null && ((future<int>)x).force()==42;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new NullableFuture1()).run();
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

class X {
   public static boolean t() {return true;}
}

