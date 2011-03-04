/**
 * @author Christian Grothoff
 * Testing arrays with user-defined element types.
 */
public class UserDefinedArray {
    const region R = [0:1];
    const dist D = dist.factory.block(R);
	public static boolean run() {
	    final E v1 = new E(1);
	    final E v2 = new E(2);
	    final E[.] a = new E[D] (point [i]) 
     			{return (i==0) ? v1 : v2; };     		
	    return future(a[0]) { a[0].v }.force() + 1 
	        == future(a[0]) { a[1].v }.force();
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new UserDefinedArray()).run();
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

	static class E extends x10.lang.Object {
	    int v;
	    E(int i) {
	       v = i;
	    }
	}
}
