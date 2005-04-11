import x10.lang.*;
/**
 * @author Christian Grothoff
 */
public class UserDefinedArray {
    const region R = [0:1];
    const distribution D = distribution.factory.block(R);
	public static boolean run() {
	    final E v1 = new E(1);
	    final E v2 = new E(2);
	    final E[.] a = new E[D] (point [i]) 
     			{return (i==0) ? v1 : v2; };     		
	    return future(a[0]) { a[0].v }.force() + 1 
	        == future(a[0]) { a[1].v }.force();
	}
	public static void main(String args[]) {
		boolean b= (new UserDefinedArray()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	static class E extends x10.lang.Object {
	    int v;
	    E(int i) {
	       v = i;
	    }
	}
}
