/**
 * Given: future nullable T x;
 * x.force() is of type nullable T and can be null 
 */
//TODO: is the "extends x10.lang.Object" necessary?
public class FutureNullable0 extends x10.lang.Object {
	public boolean run() {
		future<nullable FutureNullable0> x = future(here) { null };
		return (x.force())==null;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new FutureNullable0()).run();
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

