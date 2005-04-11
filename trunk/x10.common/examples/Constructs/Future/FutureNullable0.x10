import x10.lang.*;
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
	public static void main(String args[]) {
		boolean b= (new FutureNullable0()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

