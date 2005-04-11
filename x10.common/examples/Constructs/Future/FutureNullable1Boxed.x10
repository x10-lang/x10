import x10.lang.*;
/**
 * How will the future place be inferred?
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
	public static void main(String args[]) {
		boolean b= (new FutureNullable1Boxed()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

// class to make optimizations more difficult
class X {
   public static boolean t() { return true;}
}
