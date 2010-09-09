/**
 * A nullable future test.
 */

public class FutureNullable1Boxed  {
	public boolean run() {
		final boolean b = X.t();
		future<nullable future<FutureNullable1Boxed>> x =
			future { 
			b ?
					(( nullable future<FutureNullable1Boxed> ) null):
						(( nullable future<FutureNullable1Boxed> ) future { new FutureNullable1Boxed() })
		};
		return x.force()==null;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new FutureNullable1Boxed()).run();
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

// class to make optimizations more difficult
class X {
   public static boolean t() { return true;}
}
