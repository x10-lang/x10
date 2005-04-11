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
	public static void main(String args[]) {
		boolean b= (new NullableFuture1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

class X {
   public static boolean t() {return true;}
}

